import Vue from 'vue';
import ViewUI from 'view-design';
import Util from '../libs/util';
import VueRouter from 'vue-router';
import Cookies from 'js-cookie';
import { routers } from './router';

Vue.use(VueRouter);
const routerPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(error=> error)
}

const RouterConfig = {
    // mode: 'history',
    routes: routers
};

export const router = new VueRouter(RouterConfig);

router.beforeEach((to, from, next) => {
    ViewUI.LoadingBar.start();
    Util.title(to.meta.title);
    var name = to.name;
    let isAdminUser = false;
    try {
        const userInfoText = Cookies.get('userInfo');
        if (userInfoText) {
            const userInfo = JSON.parse(userInfoText);
            isAdminUser = Number(userInfo.type) === 1;
        }
    } catch (e) {
        isAdminUser = false;
    }
    if (!isAdminUser && (to.name === 'home_index' || to.path === '/home' || to.path.indexOf('/home?') === 0)) {
        next({ name: 'my_home_index' });
        return;
    }
    if (!isAdminUser && (to.path === '/baseMenu' || to.path.indexOf('/baseMenu/') === 0)) {
        next({ name: 'error-403' });
        return;
    }
    if (Cookies.get('locking') == '1' && name !== 'locking') {
        next({
            replace: true,
            name: 'locking'
        });
    } else if (Cookies.get('locking') == '0' && name == 'locking') {
        next(false);
    } else {
        if (!Cookies.get('userInfo') && (name != 'login' && name != 'regist')) {
            next({
                name: 'login'
            });
        } else if (Cookies.get('userInfo') && name == 'login') {
            Util.title();
            next({
                name: isAdminUser ? 'home_index' : 'my_home_index'
            });
        } else {
            Util.toDefaultPage([...routers], name, router, next);
        }
    }
});

router.afterEach((to) => {
    Util.openNewPage(router.app, to.name, to.params, to.query);
    ViewUI.LoadingBar.finish();
    window.scrollTo(0, 0);
});
