import crypto from "crypto";
import * as jwt from "jsonwebtoken";
import { storeRefreshToken } from "./authTokenStore";

const JWT_SECRET = process.env.JWT_SECRET || "segredo-jwt";
const EXPIRE_TIME = Number(process.env.EXPIRE_TIME) || 3600;
const REFRESH_TTL_MS = 7 * 24 * 60 * 60 * 1000;

export interface JwtPayload {
  sub: string;
  username: string;
}

export function generateJwtToken(payload: JwtPayload): string {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: EXPIRE_TIME });
}

export function generateRefreshToken(userId: string): string {
  const token = crypto.randomBytes(40).toString("hex");
  storeRefreshToken(token, userId, REFRESH_TTL_MS);
  return token;
}
