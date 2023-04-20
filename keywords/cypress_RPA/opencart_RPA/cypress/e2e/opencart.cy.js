context('Opencart', () => {

  it('Opencart_TC02_UpdateStock', () => {
    // Custom command defined in cypress/support/commands.js
    cy.Opencart_Admin_Login();
    cy.Opencart_Admin_Update_Product('Apple Cinema 30', 1200);
  })

})

