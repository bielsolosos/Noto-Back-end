export interface UserSummaryDto {
  id: string;
  email: string;
  username: string;
}

export interface CreateUserDto {
  email: string;
  username: string;
  password?: string;
}

export interface UserDto {
  id: string;
  email: string;
  username: string;
}

export interface changePasswordDto {
  oldPassword: string;
  newPassword: string;
}
