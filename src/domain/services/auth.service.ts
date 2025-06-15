import { getRefreshTokenData } from "../../core/authTokenStore";
import { comparePasswords } from "../../core/bcrypt";
import { generateJwtToken, generateRefreshToken } from "../../core/jwtUilts";
import { UnauthorizedError } from "../../core/messageValidationUtils";
import { UserDto } from "../models/user.model";
import * as userRepository from "../repositories/user.repository";

const API_KEY = process.env.API_KEY || "batatinha123";

export async function login(email: string, password: string, apiKey: string) {
  if (apiKey !== API_KEY) {
    throw new UnauthorizedError("API Key inválida.");
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
  };

  const token = generateJwtToken(payload);
  const refreshToken = generateRefreshToken(user.id);

  const userResponse: UserDto = {
    id: user.id,
    username: user.username,
    email: user.email,
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
  });

  return newAccessToken;
}
