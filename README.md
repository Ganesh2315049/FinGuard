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
- `/api/customer/profile`, `/api/customer/account`, `/api/customer/risk`, `/api/customer/transactions/**`
- `/api/admin/dashboard`, `/api/admin/users`, `/api/admin/transactions`, `/api/admin/fraud-rules`, `/api/admin/audit-logs`
- `/api/analyst/dashboard`, `/api/analyst/suspicious-transactions`, `/api/analyst/transactions/{id}/approve`, `/api/analyst/transactions/{id}/reject`

## Run locally

1. Start PostgreSQL: `docker compose up -d`.
2. Configure `backend/.env.example` values or use local defaults.
3. Start the API: `cd backend && mvn spring-boot:run`.
4. Install and start the UI: `cd Frontend && npm install && npm run dev`.
5. Open `http://localhost:5173`.

For a local demo, `DEMO_ACCOUNTS_ENABLED=true` provisions the fixed development identities `2315049@nec.edu.in` as `ADMIN` and `ganeshbavana26@gmail.com` as `FRAUD_ANALYST`. Their passwords are read from backend environment variables, BCrypt-hashed, and never sent to the frontend. Set `DEMO_ADMIN_PASSWORD` and `DEMO_ANALYST_PASSWORD` through a local secret/environment configuration. Set `DEMO_ACCOUNTS_ENABLED=false` in deployed environments unless demo access is explicitly required.

The configured role flow is:

```text
FinGuard -> Authentication -> Detect role
        CUSTOMER      -> /customer/dashboard -> transactions, risk, account
        ADMIN         -> /admin/dashboard    -> users, transactions, fraud controls, analytics
        FRAUD_ANALYST -> /analyst/dashboard  -> review, alerts, history, investigations
```

## Verification

```text
cd FinGuard/backend
mvn clean package -DskipTests
mvn test

cd ../Frontend
npm install
npm run build
```

Swagger is available at `http://localhost:8080/swagger-ui.html` while the API is running. Test each role by logging in with the seeded account, checking its dashboard, and manually entering another role's URL; the protected route and Spring Security API should deny access.

## Frontend routes

| Route | Role | Purpose |
| --- | --- | --- |
| `/login`, `/register` | Public | Authentication entry points |
| `/customer/dashboard` | Customer | Personal account and activity overview |
| `/customer/transactions` | Customer | Deposit, withdrawal, transfer, and history |
| `/customer/risk` | Customer | Personal risk score and signals |
| `/customer/account` | Customer | Profile and account details |
| `/admin/dashboard` | Admin | System-wide control room |
| `/admin/users` | Admin | User management view |
| `/admin/transactions` | Admin | System transaction view |
| `/admin/fraud-monitoring` | Admin | Fraud monitoring view |
| `/admin/risk-rules` | Admin | Configured fraud rules |
| `/admin/analytics` | Admin | System analytics |
| `/analyst/dashboard` | Fraud Analyst | Review operations overview |
| `/analyst/review`, `/analyst/review-queue` | Fraud Analyst | Suspicious transaction queue |
| `/analyst/alerts`, `/analyst/high-risk` | Fraud Analyst | Flagged activity |
| `/analyst/history` | Fraud Analyst | Review history view |
| `/unauthorized` | Authenticated | Access-denied state |

## Security and accessibility

- JWT signature, expiry, user ID, enabled status, and current role are checked by the backend filter.
- Spring Security protects customer, admin, and fraud analyst API namespaces independently.
- Customer repositories query by the authenticated user ID, not a client-supplied user ID.
- Transaction transfers require an idempotency key and account locking.
- The UI includes semantic labels, keyboard-focus styles, responsive navigation, loading/error/empty states, and `prefers-reduced-motion` support.
- Vercel SPA rewrites are defined in `Frontend/vercel.json` so nested routes survive browser refreshes.

## Development checklist

1. Enable local demo accounts only through environment variables.
2. Verify customer login redirects to `/customer/dashboard`.
3. Verify admin login redirects to `/admin/dashboard`.
4. Verify fraud analyst login redirects to `/analyst/dashboard`.
5. Verify each role receives `/unauthorized` for another role's route.
6. Verify customer transactions create `APPROVED`, `REVIEW`, or `BLOCKED` outcomes.
7. Verify `mvn test` and `npm run build` before deployment.
