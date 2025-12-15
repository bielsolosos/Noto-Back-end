import crypto from "crypto";
import { Request } from "express";
import * as jwt from "jsonwebtoken";
import config from "../infrastructure/application-config";
import { storeRefreshToken } from "../infrastructure/auth/authTokenStore";

const JWT_SECRET = config.jwtSecret;
const EXPIRE_TIME = config.expireTime;
const REFRESH_TTL_MS = 7 * 24 * 60 * 60 * 1000;

export interface JwtPayload {
  sub: string;
  username: string;
  role_admin: boolean;
}

export function generateJwtToken(payload: JwtPayload): string {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: EXPIRE_TIME });
}

export function generateRefreshToken(userId: string): string {
  const token = crypto.randomBytes(40).toString("hex");
  storeRefreshToken(token, userId, REFRESH_TTL_MS);
  return token;
}

export function getUserIdFromToken(token: string): string {
  const decoded = jwt.verify(token, JWT_SECRET) as JwtPayload;
  return decoded.sub;
}

export function getUsernameFromToken(token: string): string {
  const decoded = jwt.verify(token, JWT_SECRET) as JwtPayload;
  return decoded.username;
}

export function getTokenFromRequest(req: Request): string | null {
  const authHeader = req.headers["authorization"];
  if (!authHeader) return null;

  // O formato esperado é "Bearer tokenAqui"
  const parts = authHeader.split(" ");
  if (parts.length !== 2) return null;

  const scheme = parts[0];
  const token = parts[1];

  if (!/^Bearer$/i.test(scheme)) return null;

  return token;
}
