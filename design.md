This system will allow for the curation of AI generated content with minimal user intervention. The components are as follows: ServerMain, UI, Processor(s), Message Queue, Database and File Server.



# ServerMain
The entry point of the system. This will be a JSON API server that will handle creating new content, managing existing content, integrating with social media APIs, and generally tying everything together.

This will publish to the queue each time the submit operation is called. Can optionally subscribe to a queue whenever a job is finished, otherwise polling is fine for now.

It'll also integrate with 3rd party APIs to publish to IG, etc.

## Operations
`submit:`
This will submit one of several possible job requests to the message queue to be picked up by the processors, along with changing states and/or creating new rows in the tables, as needed. CREATE_CONTENT, REFINE_CONTENT, etc.

`get:`
This will fetch existing content.

`delete:`
This will delete existing content.

`post:`
This will post content to selected 3rd party.

`schedule:`
This will schedule regular content creation.

## Implementation
Node.JS, Java, Kotlin, whatever.



# UI
Basic plain HTML and CSS webpage that calls ServerMain.



# Processor
Wrapper application around ControlNet, ChatGPT, and other APIs that will actually generate the content. This is a CPU intensive application. There can be as many instances of this as needed which will subscribe to the message queue and process jobs as they come in.
Potentially, each message payload would contain a job type that corresponds to a workflow and optionally some parameters. Once the job is finished, it'll publish a message back to the queue to notify ServerMain, as well as store the assets in the DB and File Server.

## Implementation
Some sort of application to wrap around ControlNet etc.



# Message Queue
Kafka, RabbitMQ, or Redis.



# Database and File Server
MongoDB, Cassandra, some sort of NoSQL database. AWS for the File Server.


