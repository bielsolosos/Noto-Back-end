import cors from "cors";
import "dotenv/config";
import express from "express";
import rateLimit from "express-rate-limit";
import helment from "helmet";
import morgan from "morgan";
import swaggerJSDoc from "swagger-jsdoc";
import swaggerUi from "swagger-ui-express";
import authRoutes from "./api/routes/auth.routes";
import homeRoutes from "./api/routes/home.routes";
import pagesRoutes from "./api/routes/pages.routes";
import userRoutes from "./api/routes/user.routes";
import config from "./infrastructure/application-config";
import { errorHandler } from "./infrastructure/middlewares/error-handler.middleware";
import swaggerOptions from "./infrastructure/swagger.config";

// Variáveis de ambiente
const PORT = config.port;
const ADDRESS = config.address;

const app = express();
const swaggerDocs = swaggerJSDoc(swaggerOptions);

// Rate Limiting contra bot. Limita a 500 requisições a cada 15 minutos por IP
app.use(
  rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 500,
  })
);

app.use(morgan("dev")); // Logs HTTP
app.use(helment());
app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerDocs));
app.use(express.json());
app.use(cors());

app.use("/", homeRoutes);
app.use("/pages", pagesRoutes);
app.use("/users", userRoutes);
app.use("/auth", authRoutes);

app.use((req, res, next) => {
  res.status(404).json({ error: "Rota não encontrada" });
});

// Middleware global de tratamento de erros
app.use(errorHandler);

app.listen(PORT, () => {
  console.log(`Servidor rodando em ${ADDRESS}:${PORT}`);
  console.log(`Documentação da API em ${ADDRESS}:${PORT}/api-docs`);
});
