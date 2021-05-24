import { mount, createLocalVue } from '@vue/test-utils';
import Register from '../components/Register';
import Vuesax from 'vuesax';

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

// Mocking registered users
const mockUsers = {
    "id": 1,
    "users" : [
        {
            "firstName": "Helene",
            "lastName": "Newton",
            "middleName": "Angeline",
            "nickname": "non",
            "bio": "cillum nisi ullamco labore",
            "email": "angelinenewton@digitalus.com",
            "dateOfBirth": "1995-08-21",
            "phoneNumber": "(868) 442-3726",
            "homeAddress": "Albemarle Terrace",
            "password": "proident",
            "registerDate": "3/14/2021",
            "id": 1
        },
        {
            "firstName": "Sheri",
            "lastName": "Christian",
            "middleName": "Griffith",
            "nickname": "velit",
            "bio": "nulla quis elit ullamco",
            "email": "griffithchristian@digitalus.com",
            "dateOfBirth": "1995-08-21",
            "phoneNumber": "(983) 576-3532",
            "homeAddress": "Village Road",
            "password": "id",
            "registerDate": "3/14/2021",
            "id": 2
        },
        {
            "firstName": "Irwin",
            "lastName": "Lara",
            "middleName": "Fuentes",
            "nickname": "adipisicing",
            "bio": "irure reprehenderit laboris aute",
            "email": "fuenteslara@digitalus.com",
            "dateOfBirth": "1995-08-21",
            "phoneNumber": "(917) 580-3281",
            "homeAddress": "Fiske Place",
            "password": "culpa",
            "registerDate": "3/14/2021",
            "id": 3
        },
        {
            "firstName": "Eloise",
            "lastName": "Hewitt",
            "middleName": "Stella",
            "nickname": "exercitation",
            "bio": "irure ex laborum ipsum",
            "email": "stellahewitt@digitalus.com",
            "dateOfBirth": "1995-08-21",
            "phoneNumber": "(833) 599-2927",
            "homeAddress": "Java Street",
            "password": "ea",
            "registerDate": "3/14/2021",
            "id": 4
        }
    ]
}

// Mocking $route
const $route = {
    params: {
        id: 1
    }
};

let api = {
    createUser: jest.fn(),
}

let $log = {
    debug: jest.fn(),
}

beforeEach(() => {
    wrapper = mount(Register, {
        propsData: {},
        mocks: {$route, api, $log},
        stubs: ['router-link', 'router-view'],
        methods: {},

        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Register error checking', () => {
    const getRegisterMethod = jest.spyOn(Register.methods, 'createUserInfo');
    getRegisterMethod.mockResolvedValue(mockUsers);
    test('Handles empty register', () => {
        wrapper.vm.firstname = '';
        wrapper.vm.lastname = '';
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.email = '';
        wrapper.vm.password = '';
        wrapper.vm.confirm_password = '';
        wrapper.vm.dateofbirth = '';
        wrapper.vm.phonenumber = '';
        wrapper.vm.country = '';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(7);
    });

    test('Handles only email', () => {
        wrapper.vm.email = 'test@email.com';
        wrapper.vm.firstname = '';
        wrapper.vm.lastname = '';
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.password = '';
        wrapper.vm.confirm_password = '';
        wrapper.vm.dateofbirth = '';
        wrapper.vm.phonenumber = '';
        wrapper.vm.country = '';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(6);
    })
    test('Handles only password', () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.email = '';
        wrapper.vm.firstname = '';
        wrapper.vm.lastname = '';
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.dateofbirth = '';
        wrapper.vm.phonenumber = '';
        wrapper.vm.country = '';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(5);
    })

    test('Handles bad email', () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.firstname = 'bob';
        wrapper.vm.lastname = 'steve';
        wrapper.vm.dateofbirth = '15/09/0145';
        wrapper.vm.country = 'New Zealand';
        wrapper.vm.email = 'thisisnotaemail.com'
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.phonenumber = '027254871';
        wrapper.vm.validAge = jest.fn().mockResolvedValue(true);
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors).toStrictEqual(['thisisnotaemail.com'])
    })

    test('Handles large bio too large', () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.firstname = 'bob';
        wrapper.vm.lastname = 'steve';
        wrapper.vm.dateofbirth = '15/09/0145';
        wrapper.vm.country = 'New Zealand';
        wrapper.vm.email = 'thisis@email.com'
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        let info = "some info";
        wrapper.vm.bio = info.repeat(10); // 90 characters.
        wrapper.vm.phonenumber = '027254871';
        wrapper.vm.validAge = jest.fn().mockResolvedValue(true);
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors).toStrictEqual([wrapper.vm.bio])
    })
});

describe('Method Checking', () => {
    test("Country is successfully set", () => {
       wrapper.vm.setCountry("New Zealand");
       expect(wrapper.vm.country).toBe("New Zealand");
       expect(wrapper.vm.suggestCountries).toBe(false);
    });

    test("City is successfully set", () => {
        wrapper.vm.setCity("Christchurch");
        expect(wrapper.vm.city).toBe("Christchurch");
        expect(wrapper.vm.suggestCities).toBe(false);
    });
});

describe('Checking age validity', () => {
    test("Test empty date of birth", () => {
        expect(wrapper.vm.validAge("")).toBe(false);
    });

    test("Test successful age older than 13", () => {
        expect(wrapper.vm.validAge("1990-01-01")).toBe(true);
    });

    test("Test unsuccessful age younger than 13", () => {
        expect(wrapper.vm.validAge("2020-01-01")).toBe(false);
    });

    test("Test date of birth of now to be invalid", () => {
        expect(wrapper.vm.validAge(Date.now().toString())).toBe(false);
    });
});
