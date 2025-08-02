/**
 * @swagger
 * tags:
 *   name: Pages
 *   description: Endpoints relacionados às páginas
 */

/**
 * @swagger
 * /pages:
 *   post:
 *     tags:
 *       - Pages
 *     summary: Cria uma nova página
 *     operationId: createPage
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - title
 *               - content
 *             properties:
 *               title:
 *                 type: string
 *                 example: Minha página
 *               content:
 *                 type: string
 *                 description: Conteúdo Markdown da página
 *                 example: "# Título\n\nEste é o conteúdo em markdown."
 *     responses:
 *       201:
 *         description: Página criada com sucesso
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Page'
 */

/**
 * @swagger
 * /pages:
 *   get:
 *     tags:
 *       - Pages
 *     summary: Lista todas as páginas
 *     operationId: listPages
 *     responses:
 *       200:
 *         description: Lista de páginas
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Page'
 */

/**
 * @swagger
 * /pages/{id}:
 *   get:
 *     tags:
 *       - Pages
 *     summary: Obtém uma página pelo ID
 *     operationId: getPageById
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID da página
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Página encontrada
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Page'
 *       404:
 *         description: Página não encontrada
 */

/**
 * @swagger
 * /pages/{id}:
 *   put:
 *     tags:
 *       - Pages
 *     summary: Atualiza uma página pelo ID
 *     operationId: updatePage
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID da página
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               title:
 *                 type: string
 *               content:
 *                 type: string
 *                 description: Conteúdo Markdown da página
 *               archived:
 *                 type: boolean
 *     responses:
 *       200:
 *         description: Página atualizada com sucesso
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Page'
 *       404:
 *         description: Página não encontrada
 */

/**
 * @swagger
 * /pages/{id}:
 *   delete:
 *     tags:
 *       - Pages
 *     summary: Remove uma página pelo ID
 *     operationId: deletePage
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID da página
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Página removida com sucesso
 *       404:
 *         description: Página não encontrada
 */

/**
 * @swagger
 * components:
 *   schemas:
 *     Page:
 *       type: object
 *       required:
 *         - id
 *         - title
 *         - content
 *         - createdAt
 *         - updatedAt
 *         - archived
 *       properties:
 *         id:
 *           type: string
 *           example: "1234"
 *         title:
 *           type: string
 *           example: "Minha página"
 *         content:
 *           type: string
 *           description: Conteúdo Markdown da página
 *           example: "# Título\n\nConteúdo em markdown."
 *         createdAt:
 *           type: string
 *           format: date-time
 *           example: "2025-05-24T14:00:00Z"
 *         updatedAt:
 *           type: string
 *           format: date-time
 *           example: "2025-05-24T14:00:00Z"
 *         archived:
 *           type: boolean
 *           example: false
 */

import { Router } from "express";
import { authenticateToken } from "../../core/jwtRequestMiddleware";
import { validateBody } from "../../core/validateBody";
import {
  PageCreateSchema,
  PageUpdateSchema,
} from "../../domain/validators/page.validator";
import {
  createPage,
  deletePage,
  getPageById,
  listPages,
  listPagesFull,
  updatePage,
} from "../controllers/page.controller";

const router = Router();

router.use(authenticateToken);

router.post("", validateBody(PageCreateSchema), createPage);
router.get("/:id", getPageById);
router.put("/:id", validateBody(PageUpdateSchema), updatePage);
router.delete("/:id", deletePage);
router.get("/", (req, res) => {
  if (req.query.full === "true") {
    return listPagesFull(req, res);
  }
  return listPages(req, res);
});

export default router;
