 const stripe = Stripe('pk_test_51OM0bmEvzl8LgZ7DvKNyTizopC9pNlDn3arHCAd5vFlCoJodxIcm8W9f7EkCLZsEZl3lSw6WysThF5dvtyQNY8hq00WwFBnupz');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });