# Wallet Service

This exercise consists of building a proof of concept of a wallet service.

A wallet should work as a real purse:
- It stores a balance in euros. The owner can use this amount to pay for other services.
- The owner can recharge it (top-up) using a third-party payments platform (stripe, paypal, redsys, ...).
- There is no possibility to refund this money to the original payment method.

The basic structure of a wallet is its identifier and its current balance. If you think you need more fields,  just add them. We will discuss it in the interview. There are no wrong answers, we just want a starting point for a conversation.

For this exercise, you have to build endpoints for:
- Query a wallet by its identifier.
- Subtract an amount from the wallet (that is, make a charge).
- Recharge this wallet using a third-party platform.

So you can focus on these problems, you have here a maven project with a Spring Boot application. It already contains
the basic dependencies and an H2 database. There are develop and test profiles.

You can also find an implementation of the service that would call to the actual payments platform (ThirdPartyPaymentService).
You don't have to code that, just assume that this service is doing a remote request to a third-party system. 
This dummy implementation returns errors under certain conditions.

Take into account that this service would be working in a microservices environment and you would take care of high concurrency.

You can spend as much time as you need. We think you should not invest more than 3-4.
You don't have to document your code, but you can write down anything you want to explain or anything you have skipped.


# Servicio de bono monedero

El ejercicio consiste en construir una prueba de concepto de un servicio de bono monedero.
El bono monedero funciona como un monedero "real":
- Almacena un saldo en euros, que el usuario puede utilizar para pagar otros servicios.
- El usuario puede recargar dinero desde una pasarela de pagos de terceros (stripe, paypal, redsys...).
- No existe la posibilidad de devolver ese dinero al medio de pago original.

La estructura básica del monedero es su identificador y su saldo actual. Si consideras que necesitas más campos,
añádelos sin problemas y lo discutiremos en la entrevista.

El ejercicio consiste en que programes endpoints para:
- Consultar un bono por su identificador.
- Descontar saldo del monedero (un cobro).
- Recargar dinero en ese bono a través de un servicio de pago de terceros.

Para que puedas ir al grano, te damos un proyecto maven con una aplicación Spring Boot, con las dependencias básicas y una
base de datos H2. Tienes perfiles de develop y test.

Tienes también una implementación del servicio que implementaría la pasarela de pago real (ThirdPartyPaymentService).
Esa parte no tienes que programarla, asume que el servicio hace la llamada remota dada una cantidad de dinero.
Está pensado para que devuelva error bajo ciertas condiciones.

Ten en cuenta que es un servicio que conviviría en un entorno de microservicios y alta concurrencia.

Le puedes dedicar el tiempo que quieras, pero hemos estimado que 3-4 horas es suficiente para demostrar  [los requisitos del puesto.](OFERTA.md#requisitos)
 No hace falta que lo documentes pero puedes anotar todo lo que quieras como explicación o justificación de lo que hagas o dejes sin hacer.