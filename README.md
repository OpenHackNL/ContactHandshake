# Contact Handshake

_Hackathon version_ highly unstable release of the hackathon result.

An Android app to exchange contact information using NFC.
Using the app strongly encourages you to send a vCard in both directions,
rather than using Android Beam to share one contact at a time.

<!-- MarkdownTOC autolink=true bracket=round depth=2 -->

- [Different app-flows](#different-app-flows)
- [Data formats](#data-formats)

<!-- /MarkdownTOC -->


## Different app-flows

### Initiation per NFC, response per NFC.

1. Alice opens the application.
2. Alice picks a card they want to share.
3. Alice and Bob tap their phones (NFC).
4. Bob receives Alice's vCard as well as a request to choose a response card.
5. Bob picks a card they want to respond with.
6. Alice and Bob tap their phones (NFC).

### Initiation per NFC, response per online service.

1. Alice opens the application.
2. Alice picks a card they want to share.
3. Alice and Bob tap their phones (NFC).
4. Bob receives Alice's vCard as well as a request to choose a response card.
5. Bob picks a card they want to respond with.
6. Bob sends a POST request to the online service that will broker the reply.
7. Alice receives a Google Push message to their application from the online service.

### Initiation per NFC, responder does not want to share.

1. Alice opens the application.
2. Alice picks a card they want to share.
3. Alice and Bob tap their phones (NFC).
4. Bob receives Alice's vCard as well as a request to choose a response card.
5. Bob cancels or does not pick a reply.
6. Alice's phone will show the transaction has expired without a response card.

### Initiation per NFS, responder does not have the app.

1. Alice opens the application.
2. Alice picks a card they want to share.
3. Alice and Bob tap their phones (NFC).
4. Bob's phone shows the application in the Play Store to install it.
5. After Bob has completed the installation they restart the process.

## Data formats

### text/x-handshake-request

This mime-type is used to trigger a handshake.
It can be used in two ways:

1. Empty body, forces an NFC/Offline reply.
2. URL-encoded information will provide instructions on how to send a response through an online service that will act as the message broker.

For option 2, the fields are:

- **broker_post_url** - HTTP(S) URL where to send the response.
- **sender_id** - An ID for the broker online service to know who to forward the message to.
- **transaction_key** - A (random) key for the sender to verify the response received from the broker.
