# Kafka Traveler Microservices Demo: Orders

## Orders Service

Spring Boot/Kafka/Mongo Microservice, one of a set of microservices for this project. Services use Spring Kafka 2.1.6 to maintain eventually consistent data between their different `Customer` domain objects.

Originally code based on the post, [Spring Kafka - JSON Serializer Deserializer Example](https://www.codenotfound.com/spring-kafka-json-serializer-deserializer-example.html), from the [CodeNotFound.com](https://www.codenotfound.com/) Blog. Original business domain idea based on the post, [Distributed Sagas for Microservices](https://dzone.com/articles/distributed-sagas-for-microservices), on [DZone](https://dzone.com/).

## Development

For [Kakfa](https://kafka.apache.org/), I use my [garystafford/kafka-docker](https://github.com/garystafford/kafka-docker) project, a clone of the [wurstmeister/kafka-docker](https://github.com/wurstmeister/kafka-docker) project. The `garystafford/kafka-docker` [local docker-compose file](https://github.com/garystafford/kafka-docker/blob/master/docker-compose-local.yml) builds a Kafka, ZooKeeper, MongoDB, and Alpine Linux OpenJDK container.

## Commands

I debug directly from JetBrains IntelliJ. For testing the application in development, I build the jar, copy it to Alpine Linux OpenJDK `testapp` container, and run it. If testing more than one service in the same testapp container, make sure ports don't collide. Start services on different ports.

```bash
# start container if stopped
docker start kafka-docker_testapp_1

# build
./gradlew clean build

# copy
docker cp build/libs/orders-1.0.0.jar kafka-docker_testapp_1:/orders-1.0.0.jar
docker exec -it kafka-docker_testapp_1 sh

# install curl
apk update && apk add curl

# start with 'dev' profile
# same testapp container as accounts,
# so start on different port
java -jar orders-1.0.0.jar --spring.profiles.active=dev --server.port=8090 \
    --logging.level.root=DEBUG
```

## Creating Sample Data

Create sample customers with an order history.
```bash
# create sample accounts customers
curl http://localhost:8080/customers/sample

# create sample orders products
curl http://localhost:8090/products/sample

# add sample order history to orders customers
# (received from kafka `accounts.customers.change` topic)
curl http://localhost:8090/customers/samples
curl http://localhost:8090/customers/sample
curl http://localhost:8090/customers/fulfill

```

## Container Infrastructure

```text
CONTAINER ID        IMAGE                            COMMAND                  CREATED             STATUS              PORTS                                                NAMES
6079603c5d92        openjdk:8u151-jdk-alpine3.7      "sleep 6000"             4 hours ago         Up About an hour                                                         kafka-docker_testapp_1
df8914058cbb        hlebalbau/kafka-manager:latest   "/kafka-manager/bin/…"   4 hours ago         Up 4 hours          0.0.0.0:9000->9000/tcp                               kafka-docker_kafka_manager_1
5cd8f61330e0        wurstmeister/kafka:latest        "start-kafka.sh"         4 hours ago         Up 4 hours          0.0.0.0:9092->9092/tcp                               kafka-docker_kafka_1
497901621c7d        mongo:latest                     "docker-entrypoint.s…"   4 hours ago         Up 4 hours          0.0.0.0:27017->27017/tcp                             kafka-docker_mongo_1
9079612e36ad        wurstmeister/zookeeper:latest    "/bin/sh -c '/usr/sb…"   4 hours ago         Up 4 hours          22/tcp, 2888/tcp, 3888/tcp, 0.0.0.0:2181->2181/tcp   kafka-docker_zookeeper_1
```

## Orders Customer Object in MongoDB

`db.customerOrders.orders.find().pretty();`

```bson
{
	"_id" : ObjectId("5b135dd0be4176000cf30284"),
	"name" : {
		"title" : "Ms.",
		"firstName" : "Susan",
		"lastName" : "Blackstone"
	},
	"contact" : {
		"primaryPhone" : "433-544-6555",
		"secondaryPhone" : "223-445-6767",
		"email" : "susan.m.blackstone@emailisus.com"
	},
	"addresses" : [
		{
			"type" : "BILLING",
			"description" : "My CC billing address",
			"address1" : "33 Oak Avenue",
			"city" : "Nowhere",
			"state" : "VT",
			"postalCode" : "444556-9090"
		},
		{
			"type" : "SHIPPING",
			"description" : "Home Sweet Home",
			"address1" : "33 Oak Avenue",
			"city" : "Nowhere",
			"state" : "VT",
			"postalCode" : "444556-9090"
		}
	],
	"orders" : [
		{
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "PROCESSING"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "SHIPPED"
				}
			],
			"orderItems" : [
				{
					"product" : {
						"guid" : "a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d",
						"title" : "Yellow Widget",
						"description" : "Amazing Yellow Widget",
						"price" : "5.99"
					},
					"quantity" : 2
				},
				{
					"product" : {
						"guid" : "4efe33a1-722d-48c8-af8e-7879edcad2fa",
						"title" : "Purple Widget",
						"description" : "Pretty Purple Widget",
						"price" : "7.99"
					},
					"quantity" : 1
				},
				{
					"product" : {
						"guid" : "7f3c9c22-3c0a-47a5-9a92-2bd2e23f6e37",
						"title" : "Green Widget",
						"description" : "Gorgeous Green Widget",
						"price" : "11.99"
					},
					"quantity" : 1
				}
			]
		},
		{
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "PROCESSING"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "ON_HOLD",
					"note" : "Items out of stock"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "CANCELLED",
					"note" : "Ordered alternative items"
				}
			],
			"orderItems" : [
				{
					"product" : {
						"guid" : "4efe33a1-722d-48c8-af8e-7879edcad2fa",
						"title" : "Purple Widget",
						"description" : "Pretty Purple Widget",
						"price" : "7.99"
					},
					"quantity" : 3
				}
			]
		},
		{
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "PROCESSING"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "SHIPPED"
				},
				{
					"timestamp" : NumberLong("1527996051100"),
					"orderStatusType" : "RETURNED",
					"note" : "Items damaged during shipping"
				}
			],
			"orderItems" : [
				{
					"product" : {
						"guid" : "7f3c9c22-3c0a-47a5-9a92-2bd2e23f6e37",
						"title" : "Green Widget",
						"description" : "Gorgeous Green Widget",
						"price" : "11.99"
					},
					"quantity" : 4
				},
				{
					"product" : {
						"guid" : "b5efd4a0-4eb9-4ad0-bc9e-2f5542cbe897",
						"title" : "Blue Widget",
						"description" : "Brilliant Blue Widget",
						"price" : "1.99"
					},
					"quantity" : 2
				}
			]
		},
		{
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1527996053859"),
					"orderStatusType" : "APPROVED"
				}
			],
			"orderItems" : [
				{
					"product" : {
						"guid" : "d01fde07-7c24-49c5-a5f1-bc2ce1f14c48",
						"title" : "Red Widget",
						"description" : "Reliable Red Widget",
						"price" : "3.99"
					},
					"quantity" : 4
				}
			]
		}
	],
	"_class" : "com.storefront.model.CustomerOrders"
}
```

## Current Results

Output from application, on the `accounts.customers.change` topic

```text
2018-06-03 03:38:21.184  INFO [-,bc93daf404024134,bc93daf404024134,false] 188 --- [nio-8090-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka version : 1.0.1
2018-06-03 03:38:21.185  INFO [-,bc93daf404024134,bc93daf404024134,false] 188 --- [nio-8090-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId : c0518aa65f25317e
2018-06-03 03:38:21.347  INFO [-,bc93daf404024134,bc93daf404024134,false] 188 --- [nio-8090-exec-1] c.s.controller.CustomerController        : pendingOrder: Order(orderStatusEvents=[OrderStatusEvent(timestamp=1527996053859, orderStatusType=APPROVED, note=null)], orderItems=[OrderItem(product=Product(id=null, guid=a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d, title=Yellow Widget, description=Amazing Yellow Widget, price=5.99), quantity=1), OrderItem(product=Product(id=null, guid=4efe33a1-722d-48c8-af8e-7879edcad2fa, title=Purple Widget, description=Pretty Purple Widget, price=7.99), quantity=4), OrderItem(product=Product(id=null, guid=a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d, title=Yellow Widget, description=Amazing Yellow Widget, price=5.99), quantity=4)])
2018-06-03 03:38:21.350  INFO [-,bc93daf404024134,bc93daf404024134,false] 188 --- [nio-8090-exec-1] com.storefront.kafka.Sender              : sending payload='FulfillmentRequest(id=null, timestamp=1527997101346, name=Name(title=Ms., firstName=Mary, middleName=null, lastName=Smith, suffix=null), contact=Contact(primaryPhone=456-789-0001, secondaryPhone=456-222-1111, email=marysmith@yougotmail.com), address=Address(type=SHIPPING, description=Home Sweet Home, address1=1234 Main Street, address2=null, city=Anywhere, state=NY, postalCode=45455-66677), order=Order(orderStatusEvents=[OrderStatusEvent(timestamp=1527996053859, orderStatusType=APPROVED, note=null)], orderItems=[OrderItem(product=Product(id=null, guid=a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d, title=Yellow Widget, description=Amazing Yellow Widget, price=5.99), quantity=1), OrderItem(product=Product(id=null, guid=4efe33a1-722d-48c8-af8e-7879edcad2fa, title=Purple Widget, description=Pretty Purple Widget, price=7.99), quantity=4), OrderItem(product=Product(id=null, guid=a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d, title=Yellow Widget, description=Amazing Yellow Widget, price=5.99), quantity=4)]))' to topic='orders.order.fulfill'
2018-06-03 03:38:21.352  INFO [-,bc93daf404024134,bc93daf404024134,false] 188 --- [nio-8090-exec-1] c.s.controller.CustomerController        : pendingOrder: Order(orderStatusEvents=[OrderStatusEvent(timestamp=1527996053859, orderStatusType=APPROVED, note=null)], orderItems=[OrderItem(product=Product(id=null, guid=d01fde07-7c24-49c5-a5f1-bc2ce1f14c48, title=Red Widget, description=Reliable Red Widget, price=3.99), quantity=4)])
2018-06-03 03:38:21.354  INFO [-,bc93daf404024134,bc93daf404024134,false] 188 --- [nio-8090-exec-1] com.storefront.kafka.Sender              : sending payload='FulfillmentRequest(id=null, timestamp=1527997101352, name=Name(title=Ms., firstName=Susan, middleName=null, lastName=Blackstone, suffix=null), contact=Contact(primaryPhone=433-544-6555, secondaryPhone=223-445-6767, email=susan.m.blackstone@emailisus.com), address=Address(type=SHIPPING, description=Home Sweet Home, address1=33 Oak Avenue, address2=null, city=Nowhere, state=VT, postalCode=444556-9090), order=Order(orderStatusEvents=[OrderStatusEvent(timestamp=1527996053859, orderStatusType=APPROVED, note=null)], orderItems=[OrderItem(product=Product(id=null, guid=d01fde07-7c24-49c5-a5f1-bc2ce1f14c48, title=Red Widget, description=Reliable Red Widget, price=3.99), quantity=4)]))' to topic='orders.order.fulfill'
```

Output from Kafka container using the following command.

```bash
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --from-beginning --topic orders.order.fulfill
```

Kafka Consumer Output

```text
{"id":null,"timestamp":1527997101119,"name":{"title":"Mr.","firstName":"John","middleName":"S.","lastName":"Doe","suffix":"Jr."},"contact":{"primaryPhone":"555-666-7777","secondaryPhone":"555-444-9898","email":"john.doe@internet.com"},"address":{"type":"SHIPPING","description":"My home address","address1":"123 Oak Street","address2":null,"city":"Sunrise","state":"CA","postalCode":"12345-6789"},"order":{"orderStatusEvents":[{"timestamp":1527996053859,"orderStatusType":"APPROVED","note":null}],"orderItems":[{"product":{"id":null,"guid":"f3b9bdce-10d8-4c22-9861-27149879b3c1","title":"Orange Widget","description":"Opulent Orange Widget","price":9.99},"quantity":3},{"product":{"id":null,"guid":"7f3c9c22-3c0a-47a5-9a92-2bd2e23f6e37","title":"Green Widget","description":"Gorgeous Green Widget","price":11.99},"quantity":4}]}}
{"id":null,"timestamp":1527997101346,"name":{"title":"Ms.","firstName":"Mary","middleName":null,"lastName":"Smith","suffix":null},"contact":{"primaryPhone":"456-789-0001","secondaryPhone":"456-222-1111","email":"marysmith@yougotmail.com"},"address":{"type":"SHIPPING","description":"Home Sweet Home","address1":"1234 Main Street","address2":null,"city":"Anywhere","state":"NY","postalCode":"45455-66677"},"order":{"orderStatusEvents":[{"timestamp":1527996053859,"orderStatusType":"APPROVED","note":null}],"orderItems":[{"product":{"id":null,"guid":"a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d","title":"Yellow Widget","description":"Amazing Yellow Widget","price":5.99},"quantity":1},{"product":{"id":null,"guid":"4efe33a1-722d-48c8-af8e-7879edcad2fa","title":"Purple Widget","description":"Pretty Purple Widget","price":7.99},"quantity":4},{"product":{"id":null,"guid":"a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d","title":"Yellow Widget","description":"Amazing Yellow Widget","price":5.99},"quantity":4}]}}
{"id":null,"timestamp":1527997101352,"name":{"title":"Ms.","firstName":"Susan","middleName":null,"lastName":"Blackstone","suffix":null},"contact":{"primaryPhone":"433-544-6555","secondaryPhone":"223-445-6767","email":"susan.m.blackstone@emailisus.com"},"address":{"type":"SHIPPING","description":"Home Sweet Home","address1":"33 Oak Avenue","address2":null,"city":"Nowhere","state":"VT","postalCode":"444556-9090"},"order":{"orderStatusEvents":[{"timestamp":1527996053859,"orderStatusType":"APPROVED","note":null}],"orderItems":[{"product":{"id":null,"guid":"d01fde07-7c24-49c5-a5f1-bc2ce1f14c48","title":"Red Widget","description":"Reliable Red Widget","price":3.99},"quantity":4}]}}
```

The `orders.order.fulfill` sends pending orders (FulfillmentRequest) to fulfillment, via topic

```bash
kafka-topics.sh --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic orders.order.fulfill
```

## References

-   [Spring Kafka – Consumer and Producer Example](https://memorynotfound.com/spring-kafka-consume-producer-example/)
-   [Spring Kafka - JSON Serializer Deserializer Example](https://www.codenotfound.com/spring-kafka-json-serializer-deserializer-example.html)
-   [Spring for Apache Kafka: 2.1.6.RELEASE](https://docs.spring.io/spring-kafka/reference/html/index.html)
-   [Spring Data MongoDB - Reference Documentation](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
