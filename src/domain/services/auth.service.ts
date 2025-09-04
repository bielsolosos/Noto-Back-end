import { getRefreshTokenData } from "../../core/authTokenStore";
import { comparePasswords } from "../../core/bcrypt";
import config from "../../core/config";
import { generateJwtToken, generateRefreshToken } from "../../core/jwtUilts";
import { UnauthorizedError } from "../../core/messageValidationUtils";
import { UserDto } from "../models/user.model";
import * as userRepository from "../repositories/user.repository";

const API_KEY = config.apiKey;

export async function login(email: string, password: string, apiKey: string) {
  if (!apiKey || apiKey !== API_KEY) {
    throw new UnauthorizedError("A KEY TÁ ERRADA MEU AMIGO PORRA.");
  }

  const user = await userRepository.findByEmail(email);
  if (!user) {
    throw new UnauthorizedError("Credenciais inválidas.");
  }

  const passwordMatch = await comparePasswords(password, user.password);
  if (!passwordMatch) {
    throw new UnauthorizedError("Credenciais inválidas.");
  }

  const payload = {
    sub: user.id,
    username: user.username,
    role_admin: user.role_admin || false,
  };

  const token = generateJwtToken(payload);
  const refreshToken = generateRefreshToken(user.id);

  const userResponse: UserDto = {
    id: user.id,
    username: user.username,
    email: user.email,
    role_admin: user.role_admin || false,
  };

  return { token, refreshToken };
}

export async function refreshToken(refreshToken: string) {
  const tokenData = getRefreshTokenData(refreshToken);

  if (!tokenData) {
    return null;
  }

  const user = await userRepository.findById(tokenData.userId);

  const newAccessToken = generateJwtToken({
    sub: user!.id,
    username: user!.username,
    role_admin: user!.role_admin || false,
  });

  return newAccessToken;
}
