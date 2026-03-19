# Lab2

## Use cases

* **UC1.** Sign up
* **UC2.** Sign up using email and password
* **UC3.** Sign up via Google
* **UC4.** Log in
* **UC5.** Log in using email and password
* **UC6.** Log in via Google
* **UC7.** View movie info
* **UC8.** Watch movie
* **UC9.** Make payment online by card
* **UC10.** View payment info
* **UC11.** Manage subscription
* **UC12.** View subscription info
* **UC13.** Cancel subscription
* **UC14.** Buy subscription
* **UC15.** View subscription plans
* **UC16.** Get support
* **UC17.** Admin log in using email and password
* **UC18.** Support users (Admin)
* **UC19.** Manage movie (Admin)
* **UC20.** Manage subscription plan (Admin)

---

## Functional Requirements

* **FR1.** System should allow a new user to register via email + password or Google
* **FR2.** System should allow a registered user to log in via email + password** or Google
* **FR3.** System should allow user to view movie info
* **FR4.** System should allow user to view subscription plans
* **FR5.** System should allow user to buy a subscription after selecting a plan and successfully paying
* **FR6.** System should allow user to view current subscription details
* **FR7.** System should allow user to cancel subscription
* **FR8.** System should allow user to make online card payments
* **FR9.** System should allow user to view payment details
* **FR10.** System should allow users to watch movies only with an active subscription
* **FR11.** System should allow user to submit a support request
* **FR12.** System should allow admin login via email + password
* **FR13.** System should allow admin to manage movies (create/update/delete)
* **FR14.** System should allow admin to manage subscription plans (create/update)
* **FR15.** System should allow admin to process user support requests
* **FR16.** System must implement RBAC (at least roles **USER**, **ADMIN**)

---

## Non-Functional Requirements

* The application architecture must allow horizontal scaling

* Passwords are stored in encrypted form

* Access to users' personal data must be restricted and regulated by a privacy policy

* The interface must be user-friendly and intuitive. Support for different languages for further globalization (initially: Ukrainian, English)

* The system must support concurrent use by multiple users

* The average response time to user actions must not be more than 5 seconds with a load of 10,000 concurrent users

* The system should be unavailable for no more than 2 hours per month due to updates or technical work. In the event of a failure, the system should fully resume operation no later than 1 hour after the failure.

* If the system fails, all user and payment data should remain saved

## Traceability matrix

| FR / UC          | UC1   | UC2   | UC3   | UC4   | UC5   | UC6   | UC7   | UC8   | UC9   | UC10   | UC11   | UC12   | UC13   | UC14   | UC15   | UC16   | UC17   | UC18   | UC19   | UC20   |
|:--------------------------------------------|:------|:------|:------|:------|:------|:------|:------|:------|:------|:-------|:-------|:-------|:-------|:-------|:-------|:-------|:-------|:-------|:-------|:-------|
| FR1      | ✅    | ✅    | ✅    |       |       |       |       |       |       |        |        |        |        |        |        |        |        |        |        |        |
| FR2             |       |       |       | ✅    | ✅    | ✅    |       |       |       |        |        |        |        |        |        |        |        |        |        |        |
| FR3                     |       |       |       |       |       |       | ✅    |       |       |        |        |        |        |        |        |        |        |        |        |        |
| FR4               |       |       |       |       |       |       |       |       |       |        |        |        |        |        | ✅     |        |        |        |        | ✅     |
| FR5   | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |       |       | ✅    |        | ✅     |        |        | ✅     | ✅     |        |        |        |        | ✅     |
| FR6       | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |       |       | ✅    |        | ✅     | ✅     |        | ✅     |        |        |        |        |        |        |
| FR7                  | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |       |       | ✅    |        | ✅     |        | ✅     | ✅     |        |        |        |        |        |        |
| FR8                | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |       |       | ✅    |        | ✅     |        |        | ✅     |        |        |        |        |        |        |
| FR9            | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |       |       |       | ✅     | ✅     |        |        |        |        |        |        |        |        |        |
| FR10 | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |       | ✅    | ✅    |        | ✅     |        |        | ✅     |        |        |        |        |        |        |
| FR11              |       |       |       |       |       |       |       |       |       |        |        |        |        |        |        | ✅     | ✅     | ✅     |        |        |
| FR12                       |       |       |       |       |       |       |       |       |       |        |        |        |        |        |        |        | ✅     |        |        |        |
| FR13                |       |       |       |       |       |       |       |       |       |        |        |        |        |        |        |        |        |        | ✅     |        |
| FR14    |       |       |       |       |       |       |       |       |       |        |        |        |        | ✅     | ✅     |        |        |        |        | ✅     |
| FR15          |       |       |       |       |       |       |       |       |       |        |        |        |        |        |        | ✅     |        | ✅     |        |        |
| FR16      | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     | ✅     |
