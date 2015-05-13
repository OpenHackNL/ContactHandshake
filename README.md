# Contact Handshake

_Hackathon version_ highly unstable release of the hackathon result.

An Android app to exchange contact information using NFC.
Using the app strongly encourages you to send a vCard in both directions,
rather than using Android Beam to share one contact at a time.

<!-- MarkdownTOC autolink=true bracket=round depth=2 -->

- [Different app-flows](#different-app-flows)
    - [Data formats](#data-formats)
- [Icon credits](#icon-credits)
- [TODO](#todo)

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

### NDEF message for initiation

An NDEF message to initiate a handshake has 3 records.

1. A text/x-handshake-request record (see next chapter).
2. A text/x-vcard record (the actual contact info).
3. An external record (allows installing of the app).

### The text/x-handshake-request MIME-type

This MIME-type is used to initialize a handshake.
It's body is encoded similar to POST data or a query string.

It can be used in two ways:

1. direct mode, forces an NFC/Offline reply.
2. online mode, the option to use an online service that will act as the message broker will be described.

For option 1, the fields are:

- **direct** - This is a boolean field and must be set to 1.

For option 2, the fields are:

- **broker_post_url** - HTTP(S) URL where to send the response.
- **sender_id** - An ID for the broker online service to know who to forward the message to.
- **transaction_key** - A (random) key for the sender to verify the response received from the broker.

# Icon credits

https://www.iconfinder.com/icons/309089/book_communication_connection_contact_contacts_icon#size=512
https://www.iconfinder.com/icons/386290/agreement_business_contract_deal_friend_hands_handshake_hello_social_icon#size=512

# TODO

- Store handshake vCard in contact list.
- Store reference to contact in DB, to have a history of added "..." 3 days ago.
- Receive response vCard.
