import axios from 'axios';
import { getMenuList } from '@/api/index';
import lazyLoading from './lazyLoading.js';
import router from '@/router/index';
import Cookies from "js-cookie";
import { getStore } from './storage';

let util = {

};

util.title = function (title) {
    title = title || '新能源电池回收系统';
    window.document.title = title;
};

util.millsToTime = function (mills) {
    if (!mills) {
        return "";
    }
    let s = mills / 1000;
    if (s < 60) {
        return s.toFixed(0) + " 秒"
    }
    let m = s / 60;
    if (m < 60) {
        return m.toFixed(0) + " 分钟"
    }
    let h = m / 60;
    if (h < 24) {
        return h.toFixed(0) + " 小时"
    }
    let d = h / 24;
    if (d < 30) {
        return d.toFixed(0) + " 天"
    }
    let month = d / 30
    if (month < 12) {
        return month.toFixed(0) + " 个月"
    }
    let year = month / 12
    return year.toFixed(0) + " 年"

};

util.inOf = function (arr, targetArr) {
    let res = true;
    arr.forEach(item => {
        if (targetArr.indexOf(item) < 0) {
            res = false;
        }
    });
    return res;
};

util.oneOf = function (ele, targetArr) {
    if (targetArr.indexOf(ele) >= 0) {
        return true;
    } else {
        return false;
    }
};

util.getRouterObjByName = function (routers, name) {
    if (!name || !routers || !routers.length) {
        return null;
    }
    let routerObj = null;
    for (let item of routers) {
        if (item.name == name) {
            return item;
        }
        routerObj = util.getRouterObjByName(item.children, name);
        if (routerObj) {
            return routerObj;
        }
    }
    return null;
};

util.isAdminUser = function () {
    try {
        const userInfoText = Cookies.get("userInfo");
        if (userInfoText) {
            const userInfo = JSON.parse(userInfoText);
            return Number(userInfo.type) === 1;
        }
    } catch (e) {
        // ignore parse errors
    }
    return false;
};

util.isCustomerServiceUser = function () {
    try {
        const userInfoText = Cookies.get("userInfo");
        if (userInfoText) {
            const userInfo = JSON.parse(userInfoText);
            if (Number(userInfo.type) === 4) {
                return true;
            }
        }
    } catch (e) {
        // ignore parse errors
    }
    let roles = getStore("roles") || [];
    if (typeof roles === "string") {
        try {
            roles = JSON.parse(roles);
        } catch (e) {
            roles = [roles];
        }
    }
    const roleList = Array.isArray(roles) ? roles : [roles];
    return roleList.some(role => {
        const value = (role || "").toString().toUpperCase();
        return value === "ROLE_SERVICE" || value === "ROLE_CUSTOMER_SERVICE" || value.indexOf("客服") !== -1;
    });
};

util.filterSystemBaseModule = function (data) {
    // 后端优先返回基于权限的菜单；但对“客服人员(type=4)”需要额外隐藏若干系统模块。
    // 仅在客户要求的场景下进行前端屏蔽（UI 展示层），并保持其它用户不受影响。
    if (!Array.isArray(data)) return [];
    try {
        if (util.isCustomerServiceUser()) {
            // 要隐藏的模块标题（界面上红色方框），包含顶层和子菜单项
            const removeTitles = [
                "电池品类",
                "回收机构",
                "图表展示",
                "电池求购单",
                "电池出售单"
            ];
            // 深拷贝避免修改原始数据
            const cloned = JSON.parse(JSON.stringify(data));
            // 过滤顶层
            let filtered = cloned.filter(item => !removeTitles.includes(item.title));
            // 同时在子菜单层面也尝试移除（以防模块嵌套）
            filtered.forEach(item => {
                if (item.children && Array.isArray(item.children)) {
                    item.children = item.children.filter(child => !removeTitles.includes(child.title));
                }
            });
            return filtered;
        }
    } catch (e) {
        console.error('filterSystemBaseModule error', e);
    }
    return data;
};

util.handleTitle = function (vm, item) {
    return item.title;
};

util.setCurrentPath = function (vm, name) {
    let title = '';
    let isOtherRouter = false;
    vm.$store.state.app.routers.forEach(item => {
        if (item.children.length == 1) {
            if (item.children[0].name == name) {
                title = util.handleTitle(vm, item);
                if (item.name == 'otherRouter') {
                    isOtherRouter = true;
                }
            }
        } else {
            item.children.forEach(child => {
                if (child.name == name) {
                    title = util.handleTitle(vm, child);
                    if (item.name == 'otherRouter') {
                        isOtherRouter = true;
                    }
                }
            });
        }
    });
    let currentPathArr = [];
    if (name == 'home_index') {
        currentPathArr = [
            {
                title: util.handleTitle(vm, util.getRouterObjByName(vm.$store.state.app.routers, 'home_index')),
                path: '',
                name: 'home_index'
            }
        ];
    } else if ((name.indexOf('_index') >= 0 || isOtherRouter) && name !== 'home_index') {
        currentPathArr = [
            {
                title: util.handleTitle(vm, util.getRouterObjByName(vm.$store.state.app.routers, 'home_index')),
                path: '/home',
                name: 'home_index'
            },
            {
                title: title,
                path: '',
                name: name
            }
        ];
    } else {
        let currentPathObj = vm.$store.state.app.routers.filter(item => {
            if (item.children.length <= 1) {
                return item.children[0].name == name;
            } else {
                let i = 0;
                let childArr = item.children;
                let len = childArr.length;
                while (i < len) {
                    if (childArr[i].name == name) {
                        return true;
                    }
                    i++;
                }
                return false;
            }
        })[0];
        if (currentPathObj.children.length <= 1 && currentPathObj.name == 'home') {
            currentPathArr = [
                {
                    title: '首页',
                    path: '',
                    name: 'home_index'
                }
            ];
        } else if (currentPathObj.children.length <= 1 && currentPathObj.name !== 'home') {
            currentPathArr = [
                {
                    title: '首页',
                    path: '/home',
                    name: 'home_index'
                },
                {
                    title: currentPathObj.title,
                    path: '',
                    name: name
                }
            ];
        } else {
            let childObj = currentPathObj.children.filter((child) => {
                return child.name == name;
            })[0];
            currentPathArr = [
                {
                    title: '首页',
                    path: '/home',
                    name: 'home_index'
                },
                {
                    title: currentPathObj.title,
                    path: '',
                    name: currentPathObj.name
                },
                {
                    title: childObj.title,
                    path: currentPathObj.path + '/' + childObj.path,
                    name: name
                }
            ];
        }
    }
    vm.$store.commit('setCurrentPath', currentPathArr);

    return currentPathArr;
};

util.openNewPage = function (vm, name, argu, query) {
    if (!vm.$store) {
        return;
    }
    let pageOpenedList = vm.$store.state.app.pageOpenedList;
    let openedPageLen = pageOpenedList.length;
    let i = 0;
    let tagHasOpened = false;
    while (i < openedPageLen) {
        if (name == pageOpenedList[i].name) { // 页面已经打开
            vm.$store.commit('pageOpenedList', {
                index: i,
                argu: argu,
                query: query
            });
            tagHasOpened = true;
            break;
        }
        i++;
    }
    if (!tagHasOpened) {
        let tag = vm.$store.state.app.tagsList.filter((item) => {
            if (item.children) {
                return name == item.children[0].name;
            } else {
                return name == item.name;
            }
        });
        tag = tag[0];
        if (tag) {
            tag = tag.children ? tag.children[0] : tag;
            if (argu) {
                tag.argu = argu;
            }
            if (query) {
                tag.query = query;
            }
            vm.$store.commit('increateTag', tag);
        }
    }
    vm.$store.commit('setCurrentPageName', name);
};

util.toDefaultPage = function (routers, name, route, next) {
    let len = routers.length;
    let i = 0;
    let notHandle = true;
    while (i < len) {
        if (routers[i].name == name && routers[i].children && routers[i].redirect == undefined) {
            route.replace({
                name: routers[i].children[0].name
            });
            notHandle = false;
            next();
            break;
        }
        i++;
    }
    if (notHandle) {
        next();
    }
};

// 将Csv文件解析为二维数组
export const getArrayFromFile = (file) => {
    let nameSplit = file.name.split('.')
    let format = nameSplit[nameSplit.length - 1]
    return new Promise((resolve, reject) => {
        let reader = new FileReader()
        reader.readAsText(file) // 以文本格式读取
        let arr = []
        reader.onload = function (evt) {
            let data = evt.target.result // 读到的数据
            let pasteData = data.trim()
            arr = pasteData.split((/[\n\u0085\u2028\u2029]|\r\n?/g)).map(row => {
                return row.split('\t')
            }).map(item => {
                return item[0].split(',')
            })
            if (format == 'csv') resolve(arr)
            else reject(new Error('[Format Error]:不是Csv文件'))
        }
    })
}

// 将二维数组转为表格数据
export const getTableDataFromArray = (array) => {
    let columns = []
    let tableData = []
    if (array.length > 1) {
        let titles = array.shift()
        columns = titles.map(item => {
            return {
                title: item,
                key: item
            }
        })
        tableData = array.map(item => {
            let res = {}
            item.forEach((col, i) => {
                res[titles[i]] = col
            })
            return res
        })
    }
    return {
        columns,
        tableData
    }
}

util.initRouter = function (vm) {
    const constRoutes = [];
    const otherRoutes = [];

    // 404路由需要和动态路由一起加载
    const otherRouter = [{
        path: '/*',
        name: 'error-404',
        meta: {
            title: '404-页面不存在'
        },
        component: 'template/404'
    }];
    // 判断用户是否登录
    let userInfo = Cookies.get('userInfo')
    if (!userInfo) {
        // 未登录
        return;
    }
    if (!vm.$store.state.app.added) {
        // 第一次加载 读取数据
        let accessToken = getStore('accessToken');
        if (!accessToken) {
            console.warn('accessToken 为空');
            return;
        }
        // 加载菜单
        axios.get(getMenuList, { headers: { 'accessToken': accessToken } }).then(res => {
            // 检查响应数据 - axios拦截器返回的是data，不是response
            if (!res) {
                console.warn('菜单接口响应异常');
                return;
            }
            // 检查是否被重定向到登录页（未认证）
            if (res.code === 401) {
                console.warn('用户未认证，请重新登录');
                return;
            }
            // res 已经是后端返回的数据体（由拦截器处理后）
            let menuData = res.result;
            if (!menuData) {
                console.warn('菜单数据为空，请检查后端接口');
                return;
            }
            menuData = util.filterSystemBaseModule(menuData);
            util.initAllMenuData(constRoutes, menuData);
            util.initRouterNode(otherRoutes, otherRouter);
            // 添加所有主界面路由
            vm.$store.commit('updateAppRouter', constRoutes.filter(item => item.children.length > 0));
            // 添加全局路由
            vm.$store.commit('updateDefaultRouter', otherRoutes);
            // 添加菜单路由
            util.initMenuData(vm, menuData);
            // 缓存数据 修改加载标识
            window.localStorage.setItem('menuData', JSON.stringify(menuData));
            vm.$store.commit('setAdded', true);
        }).catch(err => {
            console.error('加载菜单失败:', err);
            // 清除可能损坏的缓存
            window.localStorage.removeItem('menuData');
        });
    } else {
        // 读取缓存数据
        let data = window.localStorage.getItem('menuData');
        if (!data) {
            vm.$store.commit('setAdded', false);
            // 强制重新加载菜单
            util.initRouter(vm);
            return;
        }
        try {
            let menuData = JSON.parse(data);
            if (!menuData || !Array.isArray(menuData) || menuData.length === 0) {
                // 缓存数据无效，清除并重新加载
                window.localStorage.removeItem('menuData');
                vm.$store.commit('setAdded', false);
                return;
            }
            menuData = util.filterSystemBaseModule(menuData);
            // 添加菜单路由
            util.initMenuData(vm, menuData);
        } catch (e) {
            console.error('解析缓存菜单数据失败:', e);
            window.localStorage.removeItem('menuData');
            vm.$store.commit('setAdded', false);
        }
    }
};

// 添加所有顶部导航栏下的菜单路由
util.initAllMenuData = function (constRoutes, data) {

    let allMenuData = [];
    data.forEach(e => {
        if (e.type == -1) {
            e.children.forEach(item => {
                allMenuData.push(item);
            })
        }
    })
    util.initRouterNode(constRoutes, allMenuData);
}

// 生成菜单格式数据
util.initMenuData = function (vm, data) {
    const menuRoutes = [];
    let menuData = util.filterSystemBaseModule(data);
    // 顶部菜单
    let navList = [];
    menuData.forEach(e => {
        let nav = {
            name: e.name,
            title: e.title,
            icon: e.icon
        }
        navList.push(nav);
    })
    if (navList.length < 1) {
        return;
    }
    // 存入vuex
    vm.$store.commit('setNavList', navList);
    let currNav = window.localStorage.getItem('currNav')
    if (currNav && navList.some(item => item.name === currNav)) {
        // 读取缓存title
        for (var item of navList) {
            if (item.name == currNav) {
                vm.$store.commit('setCurrNavTitle', item.title);
                break;
            }
        }
    } else {
        // 默认第一个
        currNav = navList[0].name;
        vm.$store.commit('setCurrNavTitle', navList[0].title);
    }
    vm.$store.commit('setCurrNav', currNav);
    for (var item of menuData) {
        if (item.name == currNav) {
            // 过滤
            menuData = item.children;
            break;
        }
    }
    util.initRouterNode(menuRoutes, menuData);
    // 刷新界面菜单
    vm.$store.commit('updateMenulist', menuRoutes.filter(item => item.children.length > 0));

    let tagsList = [];
    vm.$store.state.app.routers.map((item) => {
        if (item.children.length <= 1) {
            tagsList.push(item.children[0]);
        } else {
            tagsList.push(...item.children);
        }
    });
    vm.$store.commit('setTagsList', tagsList);
};

// 生成路由节点
util.initRouterNode = function (routers, data) {

    for (var item of data) {
        let menu = Object.assign({}, item);
        menu.component = lazyLoading(menu.component);

        if (item.children && item.children.length > 0) {
            menu.children = [];
            util.initRouterNode(menu.children, item.children);
        }

        let meta = {};
        // 给页面添加权限、标题、第三方网页链接
        meta.permTypes = menu.permTypes ? menu.permTypes : null;
        meta.title = menu.title ? menu.title + " - 新能源电池回收系统" : null;
        meta.url = menu.url ? menu.url : null;
        menu.meta = meta;

        routers.push(menu);
    }
};

export default util;
