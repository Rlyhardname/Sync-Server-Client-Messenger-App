
# Client-Server-message-app

## Table of context

1. Application Overview
2. How it wokrs
4. Pending features

### Aplication Overview(click to enlarge - redirects to ibb image upload)

**Active user/friends/group chats aren't hardcoded, even tho it might look like that in the image**


<img src="https://i.ibb.co/njqGcJr/base-window.png))https://i.ibb.co/njqGcJr/base-window.png" width="1000" height="500" />

### How it works(click to enlarge - redirects to ibb image upload)

<a href="https://ibb.co/ZhKSQJd"><img src="https://i.ibb.co/p3ZQHP2/2024-03-12-224455.png" alt="2024-03-12-224455" border="0"></a>

### RDB Diagram

<a href="https://ibb.co/1r0Nf6k"><img src="https://i.ibb.co/k4GTg1t/2024-03-12-231125.png" alt="2024-03-12-231125" border="0"></a><br />

### Future features?

- separate group chats. - Currently one textArea is responsible for all chats, so every authorized chat message goes in one place)
- message history.- Currently there's persistance in the server database, but no support for client persistence.
- message encryption and user details encryption
- video calls
- voice messages
- more reliable threading system via virtual threads, executorservice + threadpools, dispatcher or some other more reliable concurency mechanism.

