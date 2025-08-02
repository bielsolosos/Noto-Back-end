/*
  Warnings:

  - You are about to drop the `page` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropTable
DROP TABLE "page";

-- CreateTable
CREATE TABLE "User" (
    "user_id" TEXT NOT NULL,
    "user_email" TEXT NOT NULL,
    "user_username" TEXT NOT NULL,
    "user_password" TEXT NOT NULL,

    CONSTRAINT "User_pkey" PRIMARY KEY ("user_id")
);

-- CreateTable
CREATE TABLE "Page" (
    "page_id" TEXT NOT NULL,
    "page_title" TEXT NOT NULL,
    "page_content" TEXT NOT NULL,
    "page_createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "page_updatedAt" TIMESTAMP(3) NOT NULL,
    "page_archived" BOOLEAN NOT NULL DEFAULT false,
    "user_id" TEXT NOT NULL,

    CONSTRAINT "Page_pkey" PRIMARY KEY ("page_id")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_user_email_key" ON "User"("user_email");

-- CreateIndex
CREATE UNIQUE INDEX "User_user_username_key" ON "User"("user_username");

-- AddForeignKey
ALTER TABLE "Page" ADD CONSTRAINT "Page_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("user_id") ON DELETE RESTRICT ON UPDATE CASCADE;
