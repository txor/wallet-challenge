# Wallet Challenge

Reactive solution for the Wallet challenge, see [README.statement.md](README.statement.md) for the statement.

## Running

For running all the tests, do:

```shell
./mvnw clean verify
```

> NOTE: The IT test use _develop_ profile, payments will connect to the given payment simulator API (need Internet
> connection).

For starting up the service, do:

```shell
./mvnw spring-boot:run
```

> NOTE: I added a wallet with _id_ 999

Get the wallet:

```shell
curl  http://localhost:8090/wallets/999
```

And top it up:

```shell
curl --location 'http://localhost:8090/wallets/999/topup' \
     --header 'Content-Type: application/json' \
     --data '{
               "credit-card": "4242424242424242",
               "amount": 100
             }'
```

## Code organization

I used a minimal screaming architecture, so we have three main packages:

* ```api``` - Input data classes, the ```@Controller``` and related DTOs.
* ```model``` - Business model which is organized in _use cases_, plus ```apiclient``` to separate output interactions,
  plus ```model``` which holds _Entities_ and core classes.
* ```infra``` - Client implementation, in this case I refactored there the existing ```StripeService```. Each client
  here has to implement an _Interface_ on ```model.apiclient```.
