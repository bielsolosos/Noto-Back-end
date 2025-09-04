import winston from "winston";
import config from "./config";

const { combine, timestamp, printf, colorize } = winston.format;

// Formato personalizado para os logs
const logFormat = printf(({ level, message, timestamp, ...metadata }) => {
  const metaStr = Object.keys(metadata).length ? JSON.stringify(metadata) : "";
  return `${timestamp} [${level}]: ${message} ${metaStr}`;
});

const logger = winston.createLogger({
  level: config.logs.level,
  format: combine(timestamp(), logFormat),
  transports: [
    // Log de erros e avisos em arquivo separado
    new winston.transports.File({
      filename: config.logs.errorPath,
      level: "error",
      maxsize: config.logs.maxSize,
      maxFiles: config.logs.maxFiles,
    }),
    // Todos os logs em arquivo
    new winston.transports.File({
      filename: config.logs.path,
      maxsize: config.logs.maxSize,
      maxFiles: config.logs.maxFiles,
    }),
  ],
});

// Em desenvolvimento, também loga no console com cores
if (config.nodeEnv !== "production") {
  logger.add(
    new winston.transports.Console({
      format: combine(colorize(), logFormat),
    })
  );
}

/**
 * Interface para objetos de log estruturado
 */
export interface LogContext {
  userId?: string;
  action?: string;
  resource?: string;
  status?: string;
  error?: Error | string;
  [key: string]: any;
}

export const authLogger = {
  loginAttempt: (email: string, success: boolean, context?: LogContext) => {
    logger.info("Login attempt", {
      event: "auth_login_attempt",
      email,
      success,
      ...context,
    });
  },

  tokenVerification: (success: boolean, context?: LogContext) => {
    logger.info("Token verification", {
      event: "auth_token_verification",
      success,
      ...context,
    });
  },

  passwordChange: (userId: string, success: boolean, context?: LogContext) => {
    logger.info("Password change", {
      event: "auth_password_change",
      userId,
      success,
      ...context,
    });
  },
};

export const userLogger = {
  userCreated: (userId: string, context?: LogContext) => {
    logger.info("User created", {
      event: "user_created",
      userId,
      ...context,
    });
  },

  userUpdated: (userId: string, context?: LogContext) => {
    logger.info("User updated", {
      event: "user_updated",
      userId,
      ...context,
    });
  },

  userDeleted: (userId: string, context?: LogContext) => {
    logger.info("User deleted", {
      event: "user_deleted",
      userId,
      ...context,
    });
  },
};

export const errorLogger = {
  error: (error: Error, context?: LogContext) => {
    logger.error("Error occurred", {
      event: "error",
      error: {
        message: error.message,
        stack: error.stack,
      },
      ...context,
    });
  },

  warn: (message: string, context?: LogContext) => {
    logger.warn(message, {
      event: "warning",
      ...context,
    });
  },
};

export default logger;
