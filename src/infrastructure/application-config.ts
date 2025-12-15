import dotenv from "dotenv";

// Carrega as variáveis de ambiente
dotenv.config();

interface LogConfig {
  path: string;
  maxSize: number;
  maxFiles: number;
  level: string;
}

interface Config {
  // Database
  databaseUrl: string;

  // Server
  port: number;
  address: string;
  nodeEnv: string;

  // Auth
  jwtSecret: string;
  expireTime: number;
  apiKey: string;
  defaultPassword: string;

  // Logs
  logs: {
    path: string;
    errorPath: string;
    maxSize: number;
    maxFiles: number;
    level: string;
  };
}

const nodeEnv = getEnvWithDefault("NODE_ENV", "development");
const logLevel = getEnvWithDefault(
  "LOG_LEVEL",
  nodeEnv === "production" ? "info" : "debug"
);

/**
 * Configuração centralizada da aplicação.
 * Todas as variáveis de ambiente são validadas aqui.
 */
const config: Config = {
  // Database
  databaseUrl: getRequiredEnv("DATABASE_URL"),

  // Server
  port: parseInt(getEnvWithDefault("PORT", "8080")),
  address: getEnvWithDefault("ADDRESS", "http://localhost"),
  nodeEnv,

  // Auth
  jwtSecret: getRequiredEnv("JWT_SECRET"),
  expireTime: parseInt(getEnvWithDefault("EXPIRE_TIME", "3600")),
  apiKey: getRequiredEnv("API_KEY"),
  defaultPassword: getEnvWithDefault("DEFAULT_PASSWORD", "SenhaPadrão123"),

  // Logs
  logs: {
    path: getEnvWithDefault("LOG_PATH", "logs/combined.log"),
    errorPath: getEnvWithDefault("LOG_ERROR_PATH", "logs/error.log"),
    maxSize: parseInt(getEnvWithDefault("LOG_MAX_SIZE", "5242880")), // 5MB
    maxFiles: parseInt(getEnvWithDefault("LOG_MAX_FILES", "5")),
    level: logLevel,
  },
};

/**
 * Obtém uma variável de ambiente obrigatória
 * @throws Error se a variável não estiver definida
 */
function getRequiredEnv(key: string): string {
  const value = process.env[key];
  if (!value) {
    throw new Error(`Variável de ambiente obrigatória não definida: ${key}`);
  }
  return value;
}

/**
 * Obtém uma variável de ambiente com valor padrão
 */
function getEnvWithDefault(key: string, defaultValue: string): string {
  return process.env[key] || defaultValue;
}

export default config;
