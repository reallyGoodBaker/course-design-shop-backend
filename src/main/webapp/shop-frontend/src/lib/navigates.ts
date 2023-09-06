import Admin from "../components/Admin.svelte"
import Deal from "../components/Deal.svelte"
import Goods from "../components/Goods.svelte"
import Shopping from "../components/Shopping.svelte"

export const navigates: {[P: string]: NavigateOption} = {
    shop: {
        icon: '\ue7bf',
        title: '购物',
        component: Shopping
    },
    admin: {
        icon: '\ue7ef',
        title: '管理员',
        component: Admin
    },
    goods: {
        icon: '\uec6c',
        title: '库存',
        component: Goods
    },
    deal: {
        icon: '\ue81c',
        title: '销售记录',
        component: Deal
    }
}

export interface NavigateOption {
    icon?: string
    title: string
    component?: ConstructorOfATypedSvelteComponent
}