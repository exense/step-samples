import http from 'k6/http';
import { sleep } from 'k6';
import { check } from 'k6';

export default function () {
  const r = http.get('https://step.exense.ch', { tags: { name: "My Http request" } });

  check(r, {
    'response is status 200': (r) => r.status === 200,
  });
  
   http.get('https://step.exense.ch', { tags: { name: "My Http request 2" } });
	
  sleep(1);
}
