import { mount, createLocalVue } from '@vue/test-utils';
import Business from '../components/BusinessRegister';
import Vuesax from 'vuesax';
import {store} from "../store";

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

let $vs = {
    notify: jest.fn()
}

let $log = {
    debug: jest.fn()
}

//Mock user
const mockUser = {
    "id": 5,
    "firstName": "Rayna",
    "middleName": "YEP",
    "lastName": "Dalgety",
    "nickname": "Universal",
    "bio": "zero tolerance task-force",
    "email": "rdalgety3@ocn.ne.jp",
    "dateOfBirth": "1999-02-28",
    "phoneNumber": "+7 684 622 5902",
    "homeAddress": "44 Ramsey Court",
    "created": "2021-04-05 00:11:04",
    "role": "USER",
    "businessesAdministered": []
}

const $router = {
    push: jest.fn()
}
const checkAgeMethod = jest.spyOn(Business.methods, 'checkAge');
beforeEach(() => {
    wrapper = mount(Business, {
        propsData: {},
        mocks: {$vs, store, $router, $log},
        stubs: [],
        methods: {},
        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Business Register error checking', () => {

    test('Handles empty Register', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
    });

    test('Handles only name', () => {
        wrapper.vm.businessName = 'bestBusiness';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    })
    test('Handles only a type', () => {
        wrapper.vm.businessType = 'Charitable Organisation';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    })

    test('Handles old enough user', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
    })
});

describe('Business Register user age checking', () => {
    test('Handles to young user', () => {
        wrapper.vm.store.userDateOfBirth = "2010-01-01"
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.includes('dob')).toBe(true);
    })
});

describe('Business registration method checking', () => {
    test("Country is successfully set", () => {
        wrapper.vm.setCountry("Australia");
        expect(wrapper.vm.country).toBe("Australia");
        expect(wrapper.vm.suggestCountries).toBe(false);
    });

    test("City is successfully set", () => {
        wrapper.vm.setCity("Christchurch");
        expect(wrapper.vm.city).toBe("Christchurch");
        expect(wrapper.vm.suggestCities).toBe(false);
    });
});

describe('Checking valid age to register a business', () => {
    test("Test empty date of birth", () => {
        wrapper.vm.store.userDateOfBirth = "";
        expect(wrapper.vm.checkAge()).toBe(false);
    });

    test("Test successful age older than 16", () => {
        wrapper.vm.store.userDateOfBirth = "1980-01-01";
        expect(wrapper.vm.checkAge()).toBe(true);
    });

    test("Test unsuccessful age younger than 16", () => {
        wrapper.vm.store.userDateOfBirth = "2020-01-01";
        expect(wrapper.vm.checkAge("2020-01-01")).toBe(false);
    });

    test("Test date of birth of now to be invalid", () => {
        wrapper.vm.store.userDateOfBirth = Date.now().toString();
        expect(wrapper.vm.checkAge()).toBe(false);
    });
});

describe('Check user sessions', () => {
   test("Not logged in (no userId in store)", () => {
       wrapper.vm.store.loggedInUserId = null;
       wrapper.vm.checkLoggedIn();
       expect(wrapper.vm.$vs.notify).toBeCalled();
       expect(wrapper.vm.$router.push).toBeCalled();
   });

});

