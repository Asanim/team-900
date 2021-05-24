import {createLocalVue, shallowMount} from '@vue/test-utils';
import ActingAs from '../components/ActingAs';
import Vuesax from 'vuesax';
const localVue = createLocalVue();
let wrapper;
localVue.use(Vuesax);

let store = {
    loggedInUserId: 22,
    role: "USER",
    userName: "Wileen Tisley",
    userPrimaryBusinesses: [],
    actingAsBusinessId: null,
    actingAsBusinessName: null
}

let $log = {
    debug: jest.fn()
}

beforeEach(() => {
    wrapper = shallowMount(ActingAs, {
        propsData: {},
        mocks: {store, $log},
        stubs: ['router-link', 'router-view'],
        methods: {},
        localVue,
    });

    const getUserRoleMethod = jest.spyOn(ActingAs.methods, 'getUserRole')
    getUserRoleMethod.mockImplementation(() => {
        wrapper.vm.role = "USER";
        return wrapper.vm.role;
    });

    const getUserNameMethod = jest.spyOn(ActingAs.methods, 'getUserName');
    getUserNameMethod.mockImplementation(() => {
        return store.userName;
    });
});

afterEach(() => {
    wrapper.destroy();
});


describe('User acting as tests', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Check username is shown', () => {
        expect(wrapper.find('.user').exists()).toBe(true);
    });

    test('Check username is displaying the correct username and role', () =>  {

        wrapper.vm.store.userName = "Wileen Tisley";
        wrapper.vm.store.loggedInUserId = 22;
        expect(wrapper.vm.getUserName()).toBe('Wileen Tisley');
    });

    test('Check user primary businesses list is rendered', () =>  {
        expect(wrapper.find('#businessList').exists()).toBe(true);
    });

    test('Check selecting business in dropdown calls setActingAsBusinessId method', () => {
        wrapper.vm.setActingAsBusinessId = jest.fn();
        const busLi = wrapper.find('#businessList');
        busLi.trigger('click');
        expect(wrapper.vm.setActingAsBusinessId).toBeCalled;
    });

    test('Check selecting user calls setActingAsUser method', () => {
        //Select business
        wrapper.vm.setActingAsBusinessId = jest.fn();
        const busLi = wrapper.find('#businessList');
        busLi.trigger('click');
        const userName = wrapper.find('.user-menu');
        wrapper.vm.setActingAsUser = jest.fn();
        userName.trigger('click');
        expect(wrapper.vm.setActingAsUser).toBeCalled;
    });
});
