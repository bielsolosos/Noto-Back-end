import path from "path";

const PORT = process.env.PORT || 8080;

const swaggerOptions = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "Api Clone Notion ",
      version: "1.0.0",
      description: "Uma API exemplo documentada com Swagger",
    },
    servers: [
      {
        url: `http://localhost:${PORT}`,
      },
    ],
  },
  apis: [path.join(__dirname, "../api/routes/*.ts")], // Caminho onde estão os seus arquivos de rota com anotações
};

export default swaggerOptions;
