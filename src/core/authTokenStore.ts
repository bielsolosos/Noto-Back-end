type RefreshTokenData = {
  userId: string;
  expiresAt: Date;
};

const refreshTokensStore = new Map<string, RefreshTokenData>();

export function storeRefreshToken(
  token: string,
  userId: string,
  ttlMs: number
) {
  refreshTokensStore.set(token, {
    userId,
    expiresAt: new Date(Date.now() + ttlMs),
  });
}

export function getRefreshTokenData(token: string): RefreshTokenData | null {
  const data = refreshTokensStore.get(token);
  if (!data) return null;

  if (data.expiresAt < new Date()) {
    refreshTokensStore.delete(token);
    return null;
  }

  return data;
}

export function revokeRefreshToken(token: string) {
  refreshTokensStore.delete(token);
}
