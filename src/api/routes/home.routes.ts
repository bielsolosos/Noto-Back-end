/**
 * @swagger
 * tags:
 *   name: Home
 *   description: Endpoints relacionados à home
 * /:
 *   get:
 *     tags:
 *       - Home
 *     summary: Retorna mensagem da home
 *     operationId: getHomeMessage
 *     responses:
 *       200:
 *         description: Mensagem de sucesso
 *         content:
 *           application/json:
 *             schema:
 *               type: string
 *               example: 'API funcionando!'
 */
import { Router } from "express";
import { testHome } from "../controllers/home.controller";

const router = Router();

router.get("/", testHome);
router.get("/home", testHome);

export default router;
