# FinGuard

FinGuard is a secure financial transaction and fraud monitoring platform. It evaluates customer transactions in the Spring Boot backend, isolates customer data, and gives administrators and analysts governed operational workspaces.

## Features

### Customer
- JWT login and customer registration
- Personal balance, account, transaction history, risk score, and security activity
- Deposit, withdrawal, and transfer flows with idempotency protection
- Backend fraud evaluation with approved, review, and blocked outcomes

### Admin
- System-wide dashboard and transaction visibility
- Customer, account, fraud-rule, and audit-log views
- Configurable fraud rules through `/api/admin/fraud-rules`
- Separate control-room navigation and backend authorization

### Analyst
- Review queue for `REVIEW` and `BLOCKED` transactions
- Risk score and triggered reason visibility
- Approve or reject review decisions
- Decisions are written to the audit log with the authenticated analyst

## Architecture

```text
React + Vite frontend
        |
        v
Spring Boot REST API + JWT filter
        |
        v
Controllers -> Services -> Risk Evaluation -> Repositories
        |
        v
PostgreSQL (JPA entities)
```

Backend packages are organized as `controller`, `service`, `repository`, `entity`, `security`, `risk`, and `config`. The frontend keeps role-aware routing, navigation, views, and API calls in `Frontend/src/main.jsx` with the visual system in `Frontend/src/style.css`.

## Authentication and authorization

Login returns a signed JWT containing the user id (`uid`), email subject, and role. The filter verifies the signature, expiry, user id, enabled status, and current database role before creating the Spring Security authentication.

| Role | Access |
| --- | --- |
| `CUSTOMER` | Own profile, account, transactions, and risk information |
| `FRAUD_ANALYST` | Authorized review and risk-analysis endpoints |
| `ADMIN` | System management, fraud rules, all transaction and audit views |

Customer transaction queries derive the user from the authenticated principal and use repository ownership predicates. A customer cannot access another customer's transaction by changing an id.

## Transaction and risk flow

```text
Customer request
  -> authenticated customer controller
  -> transactional service and account lock
  -> RiskEvaluationService
  -> enabled fraud rules plus amount, time, and frequency signals
  -> APPROVED / REVIEW / BLOCKED
  -> balance update only when approved
```

Default amount signals are `<= 30,000 INR` approved, `30,001-60,000 INR` review, and `> 60,000 INR` blocked. Unusual UTC hours, more than three transactions in ten minutes, and enabled amount rules can increase the score.

## API areas

- `/api/auth/register`, `/api/auth/login`
- `/api/customer/profile`, `/api/customer/account`, `/api/customer/transactions/**`
- `/api/admin/dashboard`, `/api/admin/users`, `/api/admin/transactions`, `/api/admin/fraud-rules`, `/api/admin/audit-logs`
- `/api/analyst/dashboard`, `/api/analyst/suspicious-transactions`, `/api/analyst/transactions/{id}/approve`, `/api/analyst/transactions/{id}/reject`

## Run locally

1. Start PostgreSQL: `docker compose up -d`.
2. Configure `backend/.env.example` values or use local defaults.
3. Start the API: `cd backend && mvn spring-boot:run`.
4. Install and start the UI: `cd Frontend && npm install && npm run dev`.
5. Open `http://localhost:5173`.

For a local demo, set `DEMO_SEED=true` and a `DEMO_PASSWORD` with at least eight characters. This creates `demo.customer@finguard.local`, `demo.analyst@finguard.local`, and `demo.admin@finguard.local`. The development admin login is configured with `DEMO_ADMIN_EMAIL` and `DEMO_ADMIN_PASSWORD` and defaults to `ganeshbavana26@gmail.com` and `Ganesh@123`. These defaults are for local development only; replace them with environment values in any deployed environment.

## Verification

```text
cd FinGuard/backend
mvn test

cd ../Frontend
npm run build
```

Swagger is available at `http://localhost:8080/swagger-ui.html` while the API is running. Test each role by logging in with the seeded account, checking its dashboard, and manually entering another role's URL; the protected route and Spring Security API should deny access.
