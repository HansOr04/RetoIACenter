# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: flujo3-versionado-optimista.spec.ts >> Flujo 3 · Versionado optimista en edición >> cliente sin header If-Match en PUT recibe 428 Precondition Required
- Location: tests\flujo3-versionado-optimista.spec.ts:84:7

# Error details

```
Error: apiRequestContext.post: connect ECONNREFUSED ::1:8080
Call log:
  - → POST http://localhost:8080/api/v1/folios
    - user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.7727.15 Safari/537.36
    - accept: */*
    - accept-encoding: gzip,deflate,br
    - X-Idempotency-Key: test-ifmatch-1776719297693
    - Content-Type: application/json
    - content-length: 27

```