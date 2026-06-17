$ErrorActionPreference = "Stop"

$ImageTag = "bielsolosos/noto-api:latest"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "Building Docker Image: $ImageTag" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

docker build -t $ImageTag .

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "Build complete! Pushing image to registry..." -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

docker push $ImageTag

Write-Host "=============================================" -ForegroundColor Green
Write-Host "Successfully built and pushed $ImageTag!" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
