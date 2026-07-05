import { otherRouter } from '@/router/router';
import { router } from '@/router/index';
import Util from '@/libs/util';
import Cookies from 'js-cookie';
import Vue from 'vue';

const getDefaultOpenedTag = () => {
    try {
        const userInfoText = Cookies.get('userInfo');
        if (userInfoText) {
            const userInfo = JSON.parse(userInfoText);
            if (Number(userInfo.type) === 1) {
                return {
                    title: '首页',
                    path: '',
                    name: 'home_index'
                };
            }
        }
    } catch (e) {
        // ignore parse errors and fall through
    }
    return {
        title: '个人中心',
        path: 'myHome',
        name: 'my_home_index'
    };
};

const getIsAdminByCookie = () => {
    try {
        const userInfoText = Cookies.get('userInfo');
        if (!userInfoText) {
            return false;
        }
        const userInfo = JSON.parse(userInfoText);
        return Number(userInfo.type) === 1;
    } catch (e) {
        return false;
    }
};

const app = {
    state: {
        loading: false, // 全局加载动画
        added: false, // 加载路由标识
        navList: [], // 顶部菜单
        currNav: "", // 当前顶部菜单name
        currNavTitle: "", // 当前顶部菜单标题
        cachePage: [],
        lang: '',
        isFullScreen: false,
        openedSubmenuArr: [], // 要展开的菜单数组
        menuTheme: 'light', // 主题
        themeColor: '',
        pageOpenedList: [getDefaultOpenedTag()],
        currentPageName: '',
        currentPath: [getDefaultOpenedTag()], 
        // 面包屑数组
        menuList: [],
        routers: [
            otherRouter
        ],
        tagsList: [...otherRouter.children],
        messageCount: 0,
        // 在这里定义你不想要缓存的页面的name属性值(参见路由配置router.js)
        dontCache: ['test', 'test']
    },
    mutations: {
        // 动态添加主界面路由，需要缓存
        updateAppRouter(state, routes) {
            state.routers.push(...routes);
            router.addRoutes(routes);
        },
        // 动态添加全局路由404、500等页面，不需要缓存
        updateDefaultRouter(state, routes) {
            router.addRoutes(routes);
        },
        setLoading(state, v) {
            state.loading = v;
        },
        setAdded(state, v) {
            state.added = v;
        },
        setNavList(state, list) {
            state.navList = list;
        },
        setCurrNav(state, v) {
            state.currNav = v;
        },
        setCurrNavTitle(state, v) {
            state.currNavTitle = v;
        },
        setTagsList(state, list) {
            state.tagsList.push(...list);
        },
        updateMenulist(state, routes) {
            state.menuList = routes;
        },
        addOpenSubmenu(state, name) {
            let hasThisName = false;
            let isEmpty = false;
            if (name.length == 0) {
                isEmpty = true;
            }
            if (state.openedSubmenuArr.indexOf(name) > -1) {
                hasThisName = true;
            }
            if (!hasThisName && !isEmpty) {
                state.openedSubmenuArr.push(name);
            }
        },
        closePage(state, name) {
            state.cachePage.forEach((item, index) => {
                if (item == name) {
                    state.cachePage.splice(index, 1);
                }
            });
        },
        initCachepage(state) {
            if (localStorage.cachePage) {
                state.cachePage = JSON.parse(localStorage.cachePage);
            }
        },
        removeTag(state, name) {
            state.pageOpenedList.map((item, index) => {
                if (item.name == name) {
                    state.pageOpenedList.splice(index, 1);
                }
            });
        },
        pageOpenedList(state, get) {
            let openedPage = state.pageOpenedList[get.index];
            if (get.argu) {
                openedPage.argu = get.argu;
            }
            if (get.query) {
                openedPage.query = get.query;
            }
            state.pageOpenedList.splice(get.index, 1, openedPage);
            localStorage.pageOpenedList = JSON.stringify(state.pageOpenedList);
        },
        clearAllTags(state) {
            const defaultTag = getDefaultOpenedTag();
            state.pageOpenedList = [defaultTag];
            state.cachePage.length = 0;
            localStorage.pageOpenedList = JSON.stringify(state.pageOpenedList);
        },
        clearOtherTags(state, vm) {
            let currentName = vm.$route.name;
            let currentIndex = 0;
            state.pageOpenedList.forEach((item, index) => {
                if (item.name == currentName) {
                    currentIndex = index;
                }
            });
            if (currentIndex == 0) {
                state.pageOpenedList.splice(1);
            } else {
                state.pageOpenedList.splice(currentIndex + 1);
                state.pageOpenedList.splice(1, currentIndex - 1);
            }
            let newCachepage = state.cachePage.filter(item => {
                return item == currentName;
            });
            state.cachePage = newCachepage;
            localStorage.pageOpenedList = JSON.stringify(state.pageOpenedList);
        },
        setOpenedList(state) {
            const defaultTag = getDefaultOpenedTag();
            const isAdmin = getIsAdminByCookie();
            let openedList = localStorage.pageOpenedList ? JSON.parse(localStorage.pageOpenedList) : [defaultTag];
            if (!Array.isArray(openedList)) {
                openedList = [defaultTag];
            }
            if (!isAdmin) {
                openedList = openedList.filter(item => item && item.name !== 'home_index');
            }
            if (openedList.length < 1) {
                openedList = [defaultTag];
            }
            const hasDefaultTag = openedList.some(item => item && item.name === defaultTag.name);
            if (!hasDefaultTag) {
                openedList.unshift(defaultTag);
            }
            state.pageOpenedList = openedList;
            state.currentPath = [defaultTag];
            localStorage.pageOpenedList = JSON.stringify(state.pageOpenedList);
        },
        setCurrentPath(state, pathArr) {
            state.currentPath = pathArr;
        },
        setCurrentPageName(state, name) {
            state.currentPageName = name;
        },
        setAvatarPath(state, path) {
            localStorage.avatorImgPath = path;
        },
        switchLang(state, lang) {
            state.lang = lang;
            localStorage.lang = lang;
            Vue.config.lang = lang;
        },
        clearOpenedSubmenu(state) {
            state.openedSubmenuArr.length = 0;
        },
        setMessageCount(state, count) {
            state.messageCount = count;
        },
        increateTag(state, tagObj) {
            if (!Util.oneOf(tagObj.name, state.dontCache)) {
                state.cachePage.push(tagObj.name);
                localStorage.cachePage = JSON.stringify(state.cachePage);
            }
            state.pageOpenedList.push(tagObj);
            localStorage.pageOpenedList = JSON.stringify(state.pageOpenedList);
        }
    }
};

export default app;
