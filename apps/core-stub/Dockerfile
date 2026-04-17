FROM node:20-alpine
RUN corepack enable pnpm
WORKDIR /app
COPY package.json pnpm-lock.yaml* ./
RUN pnpm install --frozen-lockfile --prod
COPY src ./src
COPY tsconfig.json ./
RUN pnpm build
EXPOSE 4000
CMD ["node", "dist/index.js"]
