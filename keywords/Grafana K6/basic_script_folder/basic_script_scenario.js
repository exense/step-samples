import http from 'k6/http';
import { test } from './helper.js';

export const options = {
  scenarios: {
    my_web_test: {
      // the function this scenario will execute
      exec: 'webtest',
      executor: 'constant-vus',
      vus: 1,
      duration: '10s',
    },
  },
};

export function webtest() {
  test()
  http.get('https://test.k6.io/contacts.php');
}