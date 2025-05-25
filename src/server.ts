import cors from "cors";
import "dotenv/config";
import express from "express";
import helment from "helmet";
import morgan from "morgan";
import swaggerJSDoc from "swagger-jsdoc";
import swaggerUi from "swagger-ui-express";
import homeRoutes from "./api/routes/home.routes";
import pagesRoutes from "./api/routes/pages.routes";
import swaggerOptions from "./core/swagger.config";

//Variáveis de ambiente
const PORT = process.env.PORT || 8080;
const ADDRESS = process.env.ADDRESS || "http://localhost";

const app = express();
//Importa a documentação base do config
const swaggerDocs = swaggerJSDoc(swaggerOptions);

app.use(morgan("dev")); // Logs HTTP
app.use(helment());
app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerDocs));
app.use(express.json());
app.use(cors());

app.use("/", homeRoutes);
app.use("/pages", pagesRoutes);

app.listen(PORT, () => {
  console.log(`Servidor rodando em ${ADDRESS}:${PORT}`);
  console.log(`Documentação da API em ${ADDRESS}:${PORT}/api-docs`);
});
