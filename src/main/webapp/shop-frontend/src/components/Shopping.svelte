<script lang="ts">
    import {
        Expander, ContentDialog, RadioButton, Button, TextBox, NumberBox,
    } from "fluent-svelte"
    import { onMount } from "svelte"
    import { setToolBar } from "../lib/navigator"
    import { Tool } from "../lib/tool"
    import { post } from "../lib/network"

    const add = new Tool("添加", "\ued0e")
    const submit = new Tool("结算", "\ue73e", true)

    interface Goods {
        name: string
        id: string
        price: number
        amount: number
        stock: number
    }

    onMount(() => {
        updateToolbar()
    })

    let customerId = ''
    let shoppingCart: Goods[] = []

    let addOpen = false
    let searchText = ''
        ,group = 0
        ,searchList = []
        ,listSelected = -1
    
    add.onClick = () => {
        addOpen = true
    }

    let submitOpen = false
        ,isMember = false
    submit.onClick = () => {
        submitOpen = true
    }

    async function search() {
        listSelected = -1

        let list = []

        if (group) {
            list = await post('goods.matchedGoods', {
                pattern: `%${searchText}%`
            }) as any[]
        } else {
            list = await post('goods.query', {
                options: { id: searchText },
                fields: [
                    'name', 'id', 'price', 'stock'
                ]
            }) as any[]
        }

        searchList = list.filter(li => li.stock)
    }

    function updateToolbar() {
        submit.disable = !shoppingCart.length
        setToolBar([[add], [submit]])
    }

    async function checkMembership() {
        isMember = await post('customer.exists', { id: customerId }) as boolean
    }

    function calcTotal() {
        return Math.ceil(shoppingCart.reduce((pre, { price, amount }) => {
            return pre += price * amount
        }, 0) * 100) / 100
    }

    async function doShopping() {
        await post('shopping.doShopping', {
            cid: customerId,
            list: shoppingCart.map(({ amount, id }) => {
                return [ id, +amount ]
            })
        })

        submitOpen = false
        shoppingCart = []
        searchList = []
        listSelected = -1
        updateToolbar()
    }

</script>

<style>
    .splitter {
        box-sizing: border-box;
        padding: 8px 16px;
        width: 100%;
        display: grid;
        grid-template-columns: 2fr repeat(2, 1fr);
        align-items: center;
    }
</style>

{#each shoppingCart as { name, price, amount, stock }, i}
    <Expander open={true}>
        <div class="splitter">
            <div>{name}</div>
            <div>￥{price}</div>
            <div>x{amount || 0}</div>
        </div>
        <svelte:fragment slot="content">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                <div>数量</div>
                <div>库存 {stock} 件</div>
                <div style="width: 200px;">
                    <NumberBox inline on:change={() => {
                        const { amount, stock } = shoppingCart[i]
                        if (amount > stock) {
                            shoppingCart[i].amount = stock
                        }
                    }} bind:value={shoppingCart[i].amount} min={0} max={stock}/>
                </div>
            </div>
            <Button on:click={() => {
                shoppingCart = [...shoppingCart.slice(0, i), ...shoppingCart.slice(i + 1)]
                updateToolbar()
            }}>移除</Button>
        </svelte:fragment>
    </Expander>
{/each}

<ContentDialog title="添加" bind:open={addOpen}>
    <TextBox type='search' bind:value={searchText} placeholder="搜索" on:search={search}/>
    <div style="display: flex; align-items: center; gap: 12px; margin: 8px 0;">
        <div>搜索方式</div>
        <RadioButton bind:group value={0}>ID</RadioButton>
        <RadioButton bind:group value={1}>名称</RadioButton>
    </div>
    <div style="display: flex; flex-direction: column; max-height: calc(100vh - 300px); overflow: auto;">
        {#each searchList as { name, price, stock, id }, i}
            <RadioButton bind:group={listSelected} value={i}>
                <div class="splitter" style="width: 340px;">
                    <div>{name}</div>
                    <div>{price}</div>
                    <div>{stock}</div>
                </div>
            </RadioButton>
        {/each}
    </div>
    <svelte:fragment slot="footer">
        <Button variant="accent" disabled={listSelected === -1} on:click={() => {
            const {
                id, name, price, stock
            } = searchList[listSelected]
            shoppingCart = [...shoppingCart, {
                id, name, price, stock,
                amount: 1,
            }]
            updateToolbar()
            addOpen = false
        }}>
            确定
        </Button>
        <Button on:click={() => (addOpen = false)}>
            取消
        </Button>
    </svelte:fragment>
</ContentDialog>

<ContentDialog title="结算" bind:open={submitOpen}>
    <TextBox
        type='search'
        bind:value={customerId}
        placeholder="会员ID"
        on:change={checkMembership}
        on:search={checkMembership}/>
    <div style="display: flex; align-items: center; margin-top: 12px; justify-content: space-between;">
        <div style="margin-right: 8px;">应支付</div>
        <div style="font-size: 2em; line-height: 1.2em;">
            {#if isMember}
                ￥{(calcTotal() * 0.9).toFixed(2)}
            {:else}
                ￥{calcTotal()} { calcTotal() >= 200 ? ' (可加入会员)' : '' }
            {/if}
        </div>
    </div>
    <svelte:fragment slot="footer">
        <Button variant="accent" on:click={() => {
            doShopping()
        }}>
            结算
        </Button>
        <Button on:click={() => (submitOpen = false)}>
            取消
        </Button>
    </svelte:fragment>
</ContentDialog>