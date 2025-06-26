import http from 'k6/http';
import { check } from 'k6';

export default function () {
    // Home page
    var r = http.get('https://opencart-prf.stepcloud.ch/',
        { tags: { name: "OpenCart Home" } });
    check(r, {
        'response is status 200': (r) => r.status === 200,
    });

    // Adding a Macbook
    r = http.get('https://opencart-prf.stepcloud.ch/macbook',
        { tags: { name: "OpenCart Add MacBook - Step 1" } });
    check(r, {
        'response is status 200': (r) => r.status === 200,
    });

    r = http.post('https://opencart-prf.stepcloud.ch/index.php?route=checkout%2Fcart%2Fadd',
        {"quantity":1,"product_id":43},
        { tags: { name: "OpenCart Add MacBook - Step 2" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "success"': (r) => r.body.includes("success")
    });

    r = http.get('https://opencart-prf.stepcloud.ch/index.php?route=common%2Fcart%2Finfo',
        { tags: { name: "OpenCart Add MacBook - Step 3" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "MacBook"': (r) => r.body.includes("MacBook")
    });

    // Checkout
    r = http.get('https://opencart-prf.stepcloud.ch/index.php?route=checkout/checkout',
        { tags: { name: "OpenCart Checkout - Step 1" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "Guest Shipping"': (r) => r.body.includes("Guest Shipping")
    });

    r = http.get('https://opencart-prf.stepcloud.ch/index.php?route=checkout/guest',
        { tags: { name: "OpenCart Checkout - Step 2" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "First Name"': (r) => r.body.includes("First Name")
    });

    r = http.get('https://opencart-prf.stepcloud.ch/index.php?route=checkout/checkout/country&country_id=204',
        { tags: { name: "OpenCart Checkout - Step 3" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "Ticino"': (r) => r.body.includes("Ticino")
    });

    r = http.post('https://opencart-prf.stepcloud.ch/index.php?route=checkout/guest/save',
        {
            "customer_group_id": 1,
            "firstname": "Gustav",
            "lastname": "Muster",
            "email": "customer@opencart.demo",
            "telephone": "+41777777777",
            "address_1": "Bahnhofstrasse 1",
            "address_2": "",
            "city": "Zurich",
            "postcode": "8001",
            "country_id": "204",
            "zone_id": "3120",
            "company": ""
        },
        { tags: { name: "OpenCart Checkout - Step 4" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "[]"': (r) => r.body.includes("[]")
    });

    r = http.post('https://opencart-prf.stepcloud.ch/index.php?route=checkout/payment_method/save',
        {
            "comment": "",
            "agree": "1"
        },
        { tags: { name: "OpenCart Checkout - Step 5" } });

    check(r, {
        'response is status 200': (r) => r.status === 200,
        'response contains "Payment method required"': (r) => r.body.includes("Payment method required")
    });
}
