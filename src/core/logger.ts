import { Request } from "express";

export enum LogLevel {
  ERROR = "ERROR",
  WARN = "WARN",
  INFO = "INFO",
  DEBUG = "DEBUG",
}

export enum LogType {
  USER_ACTION = "USER_ACTION",
  AUTHENTICATION = "AUTHENTICATION",
  EXTERNAL_API = "EXTERNAL_API",
  SYSTEM_ERROR = "SYSTEM_ERROR",
  APPLICATION_ERROR = "APPLICATION_ERROR",
}

interface LogContext {
  userId?: string;
  username?: string;
  email?: string;
  requestId?: string;
  ip?: string;
  userAgent?: string;
}

interface LogEntry {
  timestamp: string;
  level: LogLevel;
  type: LogType;
  message: string;
  context?: LogContext;
  error?: any;
  metadata?: Record<string, any>;
}

class Logger {
  private formatLog(entry: LogEntry): string {
    return JSON.stringify({
      ...entry,
      timestamp: new Date().toISOString(),
    });
  }

  private log(level: LogLevel, type: LogType, message: string, context?: LogContext, error?: any, metadata?: Record<string, any>) {
    const entry: LogEntry = {
      timestamp: new Date().toISOString(),
      level,
      type,
      message,
      context,
      error: error ? {
        name: error.name,
        message: error.message,
        stack: error.stack,
      } : undefined,
      metadata,
    };

    const logString = this.formatLog(entry);
    
    // Output to console with appropriate level
    switch (level) {
      case LogLevel.ERROR:
        console.error(logString);
        break;
      case LogLevel.WARN:
        console.warn(logString);
        break;
      case LogLevel.INFO:
        console.info(logString);
        break;
      case LogLevel.DEBUG:
        console.debug(logString);
        break;
    }
  }

  // Extract context from Express request
  private extractContextFromRequest(req: Request): LogContext {
    const context: LogContext = {
      ip: req.ip || req.connection?.remoteAddress,
      userAgent: req.get('User-Agent'),
    };

    // Extract user info from JWT token if available
    if ((req as any).user) {
      const user = (req as any).user;
      context.userId = user.sub || user.id;
      context.username = user.username;
      context.email = user.email;
    }

    return context;
  }

  // User action logging
  logUserAction(message: string, req?: Request, metadata?: Record<string, any>) {
    const context = req ? this.extractContextFromRequest(req) : undefined;
    this.log(LogLevel.INFO, LogType.USER_ACTION, message, context, undefined, metadata);
  }

  // Authentication logging
  logAuthentication(message: string, req?: Request, metadata?: Record<string, any>) {
    const context = req ? this.extractContextFromRequest(req) : undefined;
    this.log(LogLevel.INFO, LogType.AUTHENTICATION, message, context, undefined, metadata);
  }

  // External API logging
  logExternalAPI(message: string, req?: Request, metadata?: Record<string, any>) {
    const context = req ? this.extractContextFromRequest(req) : undefined;
    this.log(LogLevel.INFO, LogType.EXTERNAL_API, message, context, undefined, metadata);
  }

  // System error logging
  logSystemError(message: string, error: any, req?: Request, metadata?: Record<string, any>) {
    const context = req ? this.extractContextFromRequest(req) : undefined;
    this.log(LogLevel.ERROR, LogType.SYSTEM_ERROR, message, context, error, metadata);
  }

  // Application error logging
  logApplicationError(message: string, error: any, req?: Request, metadata?: Record<string, any>) {
    const context = req ? this.extractContextFromRequest(req) : undefined;
    this.log(LogLevel.ERROR, LogType.APPLICATION_ERROR, message, context, error, metadata);
  }

  // Generic logging methods
  info(message: string, context?: LogContext, metadata?: Record<string, any>) {
    this.log(LogLevel.INFO, LogType.USER_ACTION, message, context, undefined, metadata);
  }

  warn(message: string, context?: LogContext, metadata?: Record<string, any>) {
    this.log(LogLevel.WARN, LogType.USER_ACTION, message, context, undefined, metadata);
  }

  error(message: string, error?: any, context?: LogContext, metadata?: Record<string, any>) {
    this.log(LogLevel.ERROR, LogType.APPLICATION_ERROR, message, context, error, metadata);
  }

  debug(message: string, context?: LogContext, metadata?: Record<string, any>) {
    this.log(LogLevel.DEBUG, LogType.USER_ACTION, message, context, undefined, metadata);
  }
}

// Export singleton instance
export const logger = new Logger();