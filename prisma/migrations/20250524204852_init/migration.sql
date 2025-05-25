-- CreateTable
CREATE TABLE "page" (
    "id" TEXT NOT NULL,
    "tittle" TEXT NOT NULL,
    "content" JSONB NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,
    "archived" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "page_pkey" PRIMARY KEY ("id")
);
