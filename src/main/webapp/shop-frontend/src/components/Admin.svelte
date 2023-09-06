<script lang="ts">
    import { navigateTo, searchListener, setToolBar } from "../lib/navigator"
    import { Button, ContentDialog, ListItem, TextBox } from "fluent-svelte"
    import { post } from "../lib/network"
    import { onMount } from "svelte";

    async function getAccounts() {
        return <string[]> await post('admin.names')
    }

    function refresh() {
        navigateTo('admin')
        editable()
        refreshData()
        open = false
        selected = -1
    }

    $: selectedName = selected > -1 ? _accounts[selected] : ''

    let open = false
    let title = ''
    let nameInput = false
    let passwdInput = false
    let passwd = ''
    let submit = Function.prototype
    let name = ''

    let _accounts: string[] = []

    async function refreshData() {
        rawData = _accounts = await getAccounts()
    }

    onMount(refreshData)

    const toolAdd = {
        icon: "\ued0e",
        text: "添加",
        onClick() {
            title = '添加管理员'
            nameInput = true
            passwdInput = true
            submit = addAdmin
            open = true
        },
    }

    async function addAdmin() {
        await post('admin.add', {
            name, passwd
        })

        refresh()
    }

    const toolDelete = {
        disable: true,
        icon: "\ue74d",
        text: "删除",
        onClick() {
            title = '删除管理员'
            nameInput = false
            passwdInput = false
            submit = rmAdmin
            open = true
        },
    }

    async function rmAdmin() {
        await post('admin.delete', {
            name: selectedName
        })

        refresh()
    }

    const toolModify = {
        disable: true,
        icon: "\ueb7e",
        text: "修改",
        onClick() {
            title = '修改密码'
            nameInput = false
            passwdInput = true
            submit = updateAdmin
            open = true
        },
    }

    async function updateAdmin() {
        await post('admin.update', {
            name: selectedName, passwd
        })

        refresh()
    }

    setToolBar([
        [ toolAdd ], [ toolDelete, toolModify ],
    ])

    function editable(bool=false) {
        toolDelete.disable = toolModify.disable = !bool
        setToolBar([
            [ toolAdd ], [ toolDelete, toolModify ],
        ])
    }

    let selected = -1
    function select(index: number) {
        selected = index
        editable(true)
    }

    let rawData = []
    searchListener.set((value: string) => {
        _accounts = rawData.filter(v => v.includes(value))
    })
</script>

{#each _accounts as name, i}
    <ListItem selected={selected === i} on:click={() => select(i)}>{name}</ListItem>
{/each}

<ContentDialog bind:open={open} {title}>
    <div style="display: flex; flex-direction: column; gap: 8px;">
        {#if nameInput}
        <TextBox bind:value={name} placeholder="账户" />
        {/if}
        {#if passwdInput}
        <TextBox bind:value={passwd} type="password" placeholder="密码" />
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