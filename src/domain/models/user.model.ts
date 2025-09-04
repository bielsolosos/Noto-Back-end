export interface UserSummaryDto {
  id: string;
  email: string;
  username: string;
  role_admin: boolean;
}

export interface CreateUserDto {
  email: string;
  username: string;
  password?: string;
  apiKey: string;
  role_admin?: boolean;
}

export interface UserDto {
  id: string;
  email: string;
  username: string;
  role_admin: boolean;
}

export interface changePasswordDto {
  oldPassword: string;
  newPassword: string;
}
