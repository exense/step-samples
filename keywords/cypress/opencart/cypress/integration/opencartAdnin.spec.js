describe('Tutorial Opencart Admin', function () {

  let productList;

  before(() => {
    cy.fixture('inventory.json').then((data) => {
      productList=data
    })
  })

  it('Opencart_TC02_UpdateStock', () => {

    // Custom command defined in cypress/support/commands.js
    cy.Opencart_Admin_Login()

    for (let row = 0; row < productList.length; row++) {
      cy.Opencart_Admin_Update_Product(productList[row].product, productList[row].quantity)
    }
  })

})

