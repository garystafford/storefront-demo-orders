# Kafka Traveler Microservices Demo: Orders

## Orders Service

Spring Boot/Kafka/Mongo Microservice, one of a set of microservices for this project. Services use Spring Kafka 2.1.6 to maintain eventually consistent data between their different `Customer` domain objects.

Originally code based on the post, [Spring Kafka - JSON Serializer Deserializer Example](https://www.codenotfound.com/spring-kafka-json-serializer-deserializer-example.html), from the [CodeNotFound.com](https://www.codenotfound.com/) Blog.

## Development

For [Kakfa](https://kafka.apache.org/), use [garystafford/kafka-docker](https://github.com/garystafford/kafka-docker) project, a clone of the [wurstmeister/kafka-docker](https://github.com/wurstmeister/kafka-docker) project. The `garystafford/kafka-docker` [local docker-compose file](https://github.com/garystafford/kafka-docker/blob/master/docker-compose-local.yml) builds a Kafka, Kafka Manager, ZooKeeper, MongoDB, Eureka Server, and Zuul.

## Commands

I develop and debug directly from JetBrains IntelliJ. The default Spring profile will start the three services on different ports.

```bash
./gradlew clean build bootRun
```

## Creating Sample Data

Create sample data for each service. Requires Kafka is running. Endpoints for Zuul, when using Docker Swarm/Stack, are different. See this [Python script](https://github.com/garystafford/storefront-kafka-docker/blob/master/refresh.py) for Zuul endpoints.

```bash
# accounts - create sample customer accounts
http http://localhost:8085/customers/sample

# orders - add sample orders to each customer
http http://localhost:8090/customers/sample/orders

# orders - send approved orders to fulfillment service
http http://localhost:8090/customers/sample/fulfill

# fulfillment - change fulfillment requests from approved to processing
http http://localhost:8095/fulfillment/sample/process

# fulfillment - change fulfillment requests from processing to shipping
http http://localhost:8095/fulfillment/sample/ship

# fulfillment - change fulfillment requests from processing to in transit
http http://localhost:8095/fulfillment/sample/in-transit

# fulfillment - change fulfillment requests from in transit to in received
http http://localhost:8095/fulfillment/sample/receive
```

## Container Infrastructure

$ docker container ls

```text
CONTAINER ID        IMAGE                                        COMMAND                  CREATED             STATUS              PORTS                                  NAMES
ccf0e9a0637d        garystafford/storefront-fulfillment:latest   "java -jar -Djava.se…"   11 minutes ago      Up 11 minutes       8080/tcp                               storefront_fulfillment.1.0mht01m6nk461q7mt1ey4zsjb
f8a4654183cb        hlebalbau/kafka-manager:latest               "/kafka-manager/bin/…"   11 minutes ago      Up 11 minutes                                              storefront_kafka_manager.1.so9h6c8veemrwlznj5zdk3sdw
fe6579d68846        garystafford/storefront-accounts:latest      "java -jar -Djava.se…"   11 minutes ago      Up 11 minutes       8080/tcp                               storefront_accounts.1.nafdn02w68nixyvmz7l46kzcq
2495802b640b        garystafford/storefront-eureka:latest        "java -jar -Djava.se…"   11 minutes ago      Up 11 minutes       8761/tcp                               storefront_eureka.1.x2tu8vg1dnizx61lwnudrnsml
5afe1e94162f        wurstmeister/kafka:latest                    "start-kafka.sh"         12 minutes ago      Up 11 minutes                                              storefront_kafka.1.n55qrkbqfueg1sgz0fb9h47qu
44a9d4dbdc4b        mongo:latest                                 "docker-entrypoint.s…"   12 minutes ago      Up 11 minutes       27017/tcp                              storefront_mongo.1.tfy3u2zi4bpcmb7372ihdjmbc
23be66801ebc        garystafford/storefront-orders:latest        "java -jar -Djava.se…"   12 minutes ago      Up 11 minutes       8080/tcp                               storefront_orders.1.bo5hfnqb9ijbfd7vp88hcs3vn
bbe4dbe00048        wurstmeister/zookeeper:latest                "/bin/sh -c '/usr/sb…"   12 minutes ago      Up 12 minutes       22/tcp, 2181/tcp, 2888/tcp, 3888/tcp   storefront_zookeeper.1.ipfwro51ob6fpls26bk14lvkt
98b8084f0162        garystafford/storefront-zuul:latest          "java -jar -Djava.se…"   12 minutes ago      Up 12 minutes       8761/tcp                               storefront_zuul.1.u4h4bxp01mcwetyoezk19ljzt
```

## Orders Customer Object in MongoDB

```text
docker exec -it $(docker ps | grep storefront_mongo | awk '{print $NF}') sh
mongo
db.customer.orders.find().pretty();
db.customer.orders.remove({});
```

```bson
{
	"_id" : ObjectId("5b188580a8d0560aab7593f6"),
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
			"guid" : "77bc4ea8-e6bf-4c9f-b43a-2d4e69863426",
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "REJECTED",
					"note" : "Primary credit card expired"
				}
			],
			"orderItems" : [
				{
					"product" : {
						"guid" : "b5efd4a0-4eb9-4ad0-bc9e-2f5542cbe897",
						"title" : "Blue Widget",
						"description" : "Brilliant Blue Widget",
						"price" : "1.99"
					},
					"quantity" : 3
				}
			]
		},
		{
			"guid" : "cb466e99-4ad2-4332-8b10-043d14528459",
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "PROCESSING"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "COMPLETED"
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
						"guid" : "b5efd4a0-4eb9-4ad0-bc9e-2f5542cbe897",
						"title" : "Blue Widget",
						"description" : "Brilliant Blue Widget",
						"price" : "1.99"
					},
					"quantity" : 3
				}
			]
		},
		{
			"guid" : "fbd6a360-047c-4294-89d5-44c729a1e7dd",
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "PROCESSING"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "ON_HOLD",
					"note" : "Items out of stock"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "CANCELLED",
					"note" : "Ordered alternative items"
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
						"guid" : "f3b9bdce-10d8-4c22-9861-27149879b3c1",
						"title" : "Orange Widget",
						"description" : "Opulent Orange Widget",
						"price" : "9.99"
					},
					"quantity" : 5
				}
			]
		},
		{
			"guid" : "ec498afc-e53c-40f7-bbd5-662476f30a9e",
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "PROCESSING"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "COMPLETED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "RETURNED",
					"note" : "Items damaged during shipping"
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
					"quantity" : 5
				}
			]
		},
		{
			"guid" : "f52e2930-ef31-44db-a53c-b7ba4ae3f5cf",
			"orderStatusEvents" : [
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "CREATED"
				},
				{
					"timestamp" : NumberLong("1528333926586"),
					"orderStatusType" : "APPROVED"
				},
				{
					"timestamp" : NumberLong("1528334452800"),
					"orderStatusType" : "PROCESSING",
					"_class" : "com.storefront.model.OrderStatusEvent"
				},
				{
					"timestamp" : NumberLong("1528334457603"),
					"orderStatusType" : "COMPLETED",
					"_class" : "com.storefront.model.OrderStatusEvent"
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
					"quantity" : 2
				},
				{
					"product" : {
						"guid" : "4efe33a1-722d-48c8-af8e-7879edcad2fa",
						"title" : "Purple Widget",
						"description" : "Pretty Purple Widget",
						"price" : "7.99"
					},
					"quantity" : 5
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
2018-06-06 21:07:43.465  INFO [-,b16677dbfc8004ff,f68d68698f81c7d9,false] 2460 --- [ntainer#0-0-C-1] com.storefront.kafka.Receiver            : received payload='CustomerOrders(id=5b18855da8d0560aab7593f1, name=Name(title=Mr., firstName=John, middleName=S., lastName=Doe, suffix=Jr.), contact=Contact(primaryPhone=555-666-7777, secondaryPhone=555-444-9898, email=john.doe@internet.com), addresses=[Address(type=BILLING, description=My cc billing address, address1=123 Oak Street, address2=null, city=Sunrise, state=CA, postalCode=12345-6789), Address(type=SHIPPING, description=My home address, address1=123 Oak Street, address2=null, city=Sunrise, state=CA, postalCode=12345-6789)], orders=null)'
2018-06-06 21:07:43.824  INFO [-,b16677dbfc8004ff,f68d68698f81c7d9,false] 2460 --- [ntainer#0-0-C-1] org.mongodb.driver.connection            : Opened connection [connectionId{localValue:2, serverValue:7}] to localhost:27017
2018-06-06 21:07:43.915  INFO [-,b16677dbfc8004ff,fe145a5213558276,false] 2460 --- [ntainer#0-0-C-1] com.storefront.kafka.Receiver            : received payload='CustomerOrders(id=5b18855da8d0560aab7593f2, name=Name(title=Ms., firstName=Mary, middleName=null, lastName=Smith, suffix=null), contact=Contact(primaryPhone=456-789-0001, secondaryPhone=456-222-1111, email=marysmith@yougotmail.com), addresses=[Address(type=BILLING, description=My CC billing address, address1=1234 Main Street, address2=null, city=Anywhere, state=NY, postalCode=45455-66677), Address(type=SHIPPING, description=Home Sweet Home, address1=1234 Main Street, address2=null, city=Anywhere, state=NY, postalCode=45455-66677)], orders=null)'
2018-06-06 21:07:43.922  INFO [-,b16677dbfc8004ff,9d8ee9e7dfcd4c85,false] 2460 --- [ntainer#0-0-C-1] com.storefront.kafka.Receiver            : received payload='CustomerOrders(id=5b18855da8d0560aab7593f3, name=Name(title=Ms., firstName=Susan, middleName=null, lastName=Blackstone, suffix=null), contact=Contact(primaryPhone=433-544-6555, secondaryPhone=223-445-6767, email=susan.m.blackstone@emailisus.com), addresses=[Address(type=BILLING, description=My CC billing address, address1=33 Oak Avenue, address2=null, city=Nowhere, state=VT, postalCode=444556-9090), Address(type=SHIPPING, description=Home Sweet Home, address1=33 Oak Avenue, address2=null, city=Nowhere, state=VT, postalCode=444556-9090)], orders=null)'
2
```

Output from Kafka container using the following command.

```bash
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --from-beginning --topic orders.order.fulfill
```

Kafka Consumer Output

```text
{"timestamp":1528334218821,"name":{"title":"Mr.","firstName":"John","middleName":"S.","lastName":"Doe","suffix":"Jr."},"contact":{"primaryPhone":"555-666-7777","secondaryPhone":"555-444-9898","email":"john.doe@internet.com"},"address":{"type":"SHIPPING","description":"My home address","address1":"123 Oak Street","address2":null,"city":"Sunrise","state":"CA","postalCode":"12345-6789"},"order":{"guid":"facb2d0c-4ae7-4d6c-96a0-293d9c521652","orderStatusEvents":[{"timestamp":1528333926586,"orderStatusType":"CREATED","note":null},{"timestamp":1528333926586,"orderStatusType":"APPROVED","note":null}],"orderItems":[{"product":{"id":null,"guid":"7f3c9c22-3c0a-47a5-9a92-2bd2e23f6e37","title":"Green Widget","description":"Gorgeous Green Widget","price":11.99},"quantity":5}]}}
{"timestamp":1528334218824,"name":{"title":"Ms.","firstName":"Mary","middleName":null,"lastName":"Smith","suffix":null},"contact":{"primaryPhone":"456-789-0001","secondaryPhone":"456-222-1111","email":"marysmith@yougotmail.com"},"address":{"type":"SHIPPING","description":"Home Sweet Home","address1":"1234 Main Street","address2":null,"city":"Anywhere","state":"NY","postalCode":"45455-66677"},"order":{"guid":"5f900d92-e2a2-484f-8e9c-7e0a24b093fd","orderStatusEvents":[{"timestamp":1528333926586,"orderStatusType":"CREATED","note":null},{"timestamp":1528333926586,"orderStatusType":"APPROVED","note":null}],"orderItems":[{"product":{"id":null,"guid":"a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d","title":"Yellow Widget","description":"Amazing Yellow Widget","price":5.99},"quantity":5},{"product":{"id":null,"guid":"a9d5a5c7-4245-4b4e-b1c3-1d3968f36b2d","title":"Yellow Widget","description":"Amazing Yellow Widget","price":5.99},"quantity":4}]}}
{"timestamp":1528334218838,"name":{"title":"Ms.","firstName":"Susan","middleName":null,"lastName":"Blackstone","suffix":null},"contact":{"primaryPhone":"433-544-6555","secondaryPhone":"223-445-6767","email":"susan.m.blackstone@emailisus.com"},"address":{"type":"SHIPPING","description":"Home Sweet Home","address1":"33 Oak Avenue","address2":null,"city":"Nowhere","state":"VT","postalCode":"444556-9090"},"order":{"guid":"f52e2930-ef31-44db-a53c-b7ba4ae3f5cf","orderStatusEvents":[{"timestamp":1528333926586,"orderStatusType":"CREATED","note":null},{"timestamp":1528333926586,"orderStatusType":"APPROVED","note":null}],"orderItems":[{"product":{"id":null,"guid":"d01fde07-7c24-49c5-a5f1-bc2ce1f14c48","title":"Red Widget","description":"Reliable Red Widget","price":3.99},"quantity":2},{"product":{"id":null,"guid":"4efe33a1-722d-48c8-af8e-7879edcad2fa","title":"Purple Widget","description":"Pretty Purple Widget","price":7.99},"quantity":5}]}}
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
