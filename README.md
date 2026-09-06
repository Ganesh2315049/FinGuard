# FinGuard

FinGuard is a production-style portfolio foundation for a financial transaction risk and fraud detection platform.

## Current capabilities
- Spring Boot 3 / Java 17 / Maven backend
- PostgreSQL-ready JPA model for users, accounts, transactions, risk evaluations, fraud rules, audit logs, and idempotency records
- BCrypt registration and JWT login foundation
- Rule-based risk evaluation service with configurable amount rules
- Vite + React responsive dashboard shell
- Docker Compose PostgreSQL for local development

## Run locally
1. Start PostgreSQL with `docker compose up -d`.
2. Set backend variables from `backend/.env.example`, or use the development defaults.
3. Run `cd backend && mvn spring-boot:run`.
4. Run `cd frontend && npm install && npm run dev`.
5. Open `http://localhost:5173`.

For a controlled local demo, set `DEMO_SEED=true` and `DEMO_PASSWORD` (at least 8 characters) before starting the backend. This creates BCrypt-backed accounts for `demo.customer@finguard.local`, `demo.analyst@finguard.local`, and `demo.admin@finguard.local`. Keep demo seeding disabled in production and never commit the password.

Swagger is available at `http://localhost:8080/swagger-ui.html` when the backend is running.

## Architecture
The backend separates controllers, services, repositories, entities, security, and risk evaluation. Financial amounts use `BigDecimal`; account locking and transactional service methods are intended to protect balance consistency. The frontend calls the backend through `VITE_API_BASE_URL`.

## Planned next slices
Customer deposit/withdrawal/transfer endpoints, analyst review workflows, admin rule management, audit APIs, integration tests, and deployment configuration.
