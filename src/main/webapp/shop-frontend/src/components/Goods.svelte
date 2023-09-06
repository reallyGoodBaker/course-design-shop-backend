<script lang="ts">
    import { Button, ContentDialog, TextBox, ListItem } from "fluent-svelte";
    import { searchListener, setToolBar } from "../lib/navigator"
    import { Tool } from "../lib/tool"
    import { post } from "../lib/network";
    import { onMount } from "svelte";

    const add = new Tool('添加', '\ued0e')
    const remove = new Tool('移除', '\ue74d', true)
    const edit = new Tool('修改', '\ueb7e', true)

    let open = false
    let submit = Function.prototype
    let idInput = false
    let dataInput = false
    let title = ''

    let id = '', name = '', price = '', stock = ''

    async function refresh() {
        remove.disable = true
        edit.disable = true
        selected = -1
        await updateData({ price: '..' })
        render()
        setToolBar([[add], [remove, edit]])
    }

    function addGoods() {
        title = '添加货物'
        idInput = true
        dataInput = true
        name = price = stock = ''
        submit = async () => {
            await post('goods.add', {
                id, name, price: +price, stock: +stock
            })

            open = false
            await refresh()
        }
        open = true
    }

    add.onClick = addGoods

    function deleteGoods() {
        title = '删除货物'
        idInput = false
        dataInput = false
        submit = async () => {
            await post('goods.delete', {
                id: listData[selected].id
            })

            open = false
            await refresh()
        }
        open = true
    }

    remove.onClick = deleteGoods

    let rawData: any[] = []
    let listData: {id: string, name: string, price: number, stock: number}[] = []

    async function updateData(
        options={},
        fields=['id', 'name', 'price', 'stock']
    ) {
        rawData = <string[]> await post('goods.query', {
            options, fields
        })
    }

    function render() {
        listData = rawData
    }

    searchListener.set((value: string) => {
        listData = rawData.filter(v => {
            return v.name.includes(value)
        })
    })

    onMount(async () => {
        await refresh()
    })

    let selected = -1

    function updateGoods() {
        title = '修改货物'
        idInput = false
        dataInput = true

        const current = listData[selected]
        name = current.name
        price = String(current.price)
        stock = String(current.stock)
        submit = async () => {
            await post('goods.update', {
                id: listData[selected].id,
                name,
                price: +price,
                stock: +stock
            })

            open = false
            await refresh()
        }
        open = true
    }

    edit.onClick = updateGoods
</script>

<style>
    .spliter {
        box-sizing: border-box;
        padding: 8px 16px;
        color: #fff;
        width: 100%;
        display: grid;
        grid-template-columns: 2fr repeat(2, 1fr);
        align-items: center;
    }

    .spliter:hover {
        background-color: #212121;
    }

    .spliter:active, .spliter.selected {
        background-color: #2a2a2a;
    }

    .spliter.header {
        padding: 16px;
        user-select: none;
    }
</style>

<ContentDialog bind:open={open} {title}>
    <div style="display: flex; flex-direction: column; gap: 8px;">
        {#if idInput}
        <TextBox bind:value={id} placeholder="货物id" />
        {/if}
        {#if dataInput}
        <TextBox bind:value={name} placeholder="货物名称" />
        <TextBox bind:value={price} placeholder="价格" />
        <TextBox bind:value={stock} placeholder="数量" />
        {/if}
    </div>
    <svelte:fragment slot="footer">
        <Button variant="accent" on:click={() => {
            submit()
        }}>
            确定
        </Button>
        <Button on:click={() => (open = false)}>
            取消
        </Button>
    </svelte:fragment>
</ContentDialog>

<div>
    <div class="spliter header">
        <span>名称</span>
        <span>价格</span>
        <span>库存量</span>
    </div>
    <div style="height: calc(100vh - 174px); overflow: auto;">
    {#each listData as { name, price, stock }, i}
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="spliter {selected === i ? 'selected' : ''}" on:click={() => {
            selected = i
            remove.disable = false
            edit.disable = false
            setToolBar([[add], [remove, edit]])
        }}>
            <div>{name}</div>
            <div>{price.toFixed(2)}</div>
            <div>{stock}</div>
        </div>
    {/each}
    </div>
</div>