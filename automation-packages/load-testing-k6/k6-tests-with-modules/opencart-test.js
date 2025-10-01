import {test} from './helper.js';

export const options = {
    discardResponseBodies: false,
    scenarios: {
        contacts: {
            executor: 'ramping-vus',
            startVUs: 1,
            stages: [
                {duration: "5s", target: 10}
            ],
            gracefulRampDown: '1m',
        },
    },
};

export default function () {
    test()
}
