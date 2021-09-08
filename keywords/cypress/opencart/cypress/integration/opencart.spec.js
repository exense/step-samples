context('Opencart', () => {

  it('Opencart_TC01_PlaceOrder', () => {
    Cypress.config('defaultCommandTimeout', 10000);	  
    cy.visit("https://opencart-prf.exense.ch/")
	cy.contains('Desktops').click()
	cy.contains('Mac').click()
	cy.contains('iMac').click()
	cy.contains('Add to Cart').click()
	cy.contains('1 item').click()
	cy.contains('View Cart').click()
	cy.contains('Checkout').click()
	cy.contains('Guest Checkout').click()
	cy.get('#button-account').click()
	cy.get('#input-payment-firstname').type('Gustav')
	cy.get('#input-payment-lastname').type('Muster')
	cy.get('#input-payment-email').type('gustav@muster.ch')
	cy.get('#input-payment-telephone').type('+41777777777')
	cy.get('#input-payment-address-1').type('Bahnhofstrasse 1')
	cy.get('#input-payment-city').type('Zurich')
	cy.get('#input-payment-postcode').type('8001')
	cy.get('#input-payment-country').select('Switzerland')
	cy.get('#input-payment-zone').select('Zürich')
	cy.get('#button-guest').click()
	cy.get('#button-shipping-method').click()
	cy.get('.pull-right > [type="checkbox"]').click()
	cy.get('#button-payment-method').click()
	cy.get('#button-confirm').click()
	cy.contains('Your order has been placed!')
  })

})

