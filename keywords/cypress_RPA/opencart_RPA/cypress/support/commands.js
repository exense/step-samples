Cypress.config('defaultCommandTimeout', 10000);

Cypress.Commands.add('Opencart_Admin_Login', () => {
  cy.visit('https://opencart-prf.exense.ch/admin/')
  cy.get('#input-username').type('demo')
  cy.get('#input-password').type('demo')
  cy.get('button').contains('Login').click()
  cy.get('a').contains('Catalog').click()
})


Cypress.Commands.add('Opencart_Admin_Update_Product', (productName, quantity) => {
  cy.get('a').contains('Products').click()
  cy.get('td').contains(productName).siblings().find('[data-original-title="Edit"]').click()
  cy.get('a').contains('Data').click()
  cy.get('#input-quantity').clear().type(quantity)
  cy.get('[data-original-title="Save"]').click()
})