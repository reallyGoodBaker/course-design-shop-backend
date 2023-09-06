<script lang="ts">
    import { onMount } from 'svelte';
    import { setToolBar } from '../lib/navigator'
    import { post } from '../lib/network';
    import { Tool } from '../lib/tool'
    import {
        Expander, Button, ContentDialog, RadioButton, TextBox, CalendarView, NumberBox

    } from 'fluent-svelte'

    const exportTool = new Tool('导出', '\uede1')
    const queryTool = new Tool('查询', '\uf78b')
    
    setToolBar([[queryTool], [exportTool]])

    async function queryDealHistory() {
        deals = (await post('deal.query', {
            conditions: {
                cost: '..'
            },
            fields: [
                'detail', 'cost', 'date', 'cid'
            ],
            options: {
                limit: 50,
                offset: 0
            }
        }) as any[]).map(deal => {
            const date = new Date()
            date.setTime(+deal.date)
            deal.date = date.toLocaleString()
            deal.detail = JSON.parse(deal.detail)

            return deal
        })
    }

    let deals = []

    queryTool.onClick = () => {
        open = true
    }

    onMount(() => {
        queryDealHistory()
    })

    let open = false
        ,group = 0
        ,cid = ''
        ,cost = [0, 0]
        ,dates = []
        ,limit = 50
        ,offset = 0

    async function doQuery() {
        const data: any = {
            fields: [
                'detail', 'cost', 'date', 'cid'
            ],
            options: {
                limit: +limit,
                offset: +offset
            }
        }

        switch (group) {
            case 0:
                data.conditions = { cid }
                break

            case 1:
                data.conditions = { cost: `${cost[0]}..${cost[1]}` }
                break

            case 2:
                dates = dates.sort((a, b) => a - b)
                data.conditions = { date: `${dates[0]}..${dates[1]}` }
                break
        }

        deals = (await post('deal.query', data) as any[]).map(deal => {
            const date = new Date()
            date.setTime(+deal.date)
            deal.date = date.toLocaleString()
            deal.detail = JSON.parse(deal.detail)

            return deal
        })

        open = false
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

<div style="height: calc(100vh - 124px); overflow: auto;">
{#each deals as { cost, date, detail, cid }}
<Expander>
    <div class="splitter">
        <div>{date}</div>
        <div>￥{cost}</div>
        <div>{cid || ''}</div>
    </div>
    <svelte:fragment slot="content">
        {#each detail as { item: { name, price }, amount }}
            <div class="splitter" style="padding: 8px 16px;">
                <div>{name}</div>
                <div>￥{price}</div>
                <div>x{amount}</div>
            </div>
        {/each}
    </svelte:fragment>
</Expander>
{/each}
</div>

<ContentDialog bind:open={open} title='查询'>
    <div style="display: flex; flex-direction: column; gap: 8px;">
        <RadioButton bind:group value={0}>用户id</RadioButton>
        <RadioButton bind:group value={1}>消费金额</RadioButton>
        <RadioButton bind:group value={2}>时间</RadioButton>
        {#if group === 0}
        <TextBox bind:value={cid} placeholder='顾客id'/>
        {:else if group === 1}
        <div style="display: flex; justify-content: space-between; gap: 20px;">
            <TextBox bind:value={cost[0]} placeholder='区间起点'/>
            -
            <TextBox bind:value={cost[1]} placeholder='区间终点'/>
        </div>
        {:else}
        <CalendarView multiple bind:value={dates} on:change={() => {
            dates = dates.slice(-2)
        }}/>
        {/if}
        <div style="display: flex; justify-content: space-between; gap: 20px;">
            <div style="width: 200px;">查询数量</div>
            <NumberBox inline bind:value={limit}/>
        </div>
        <div style="display: flex; justify-content: space-between; gap: 20px;">
            <div style="width: 200px;">偏移</div>
            <NumberBox inline bind:value={offset}/>
        </div>
    </div>
    <svelte:fragment slot="footer">
        <Button variant="accent" on:click={doQuery}>
            确定
        </Button>
        <Button on:click={() => (open = false)}>
            取消
        </Button>
    </svelte:fragment>
</ContentDialog>