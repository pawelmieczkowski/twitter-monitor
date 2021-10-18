import "./Chart.scss"
import React, { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import moment from 'moment';
import { findFullLangName } from "../utils/LangLibrary";

export const Chart = ({ langData, totalOccurrence, dataSet, setDataSet }) => {

    const INITIALLY_SELECTED_QTY = 3;
    const DEFAULT_SHOW_LANG_QTY = 20;

    const [showLangQty, setshowLangQty] = useState(DEFAULT_SHOW_LANG_QTY);
    const [selected, setSelected] = useState([]);
    const [selectedData, setSelectedData] = useState([]);

    /*     set most common languages to be selected by default */

    useEffect(() => {
        const initiallySelected = [];
        for (let i = 0; i < INITIALLY_SELECTED_QTY; i++) {
            if (totalOccurrence[i])
                initiallySelected.push(totalOccurrence[i][0]);
        }
        setSelected(initiallySelected)
    }, []) // eslint-disable-line

    /*     transform data for chart needs */
    useEffect(() => {
        let newData = [];
        for (let i = 0; i < langData.length; i++) {
            let tempArr = []
            tempArr.push({ timestamp: Date.parse(langData[i].timestamp) })
            for (let j = 0; j < langData[i].records.length; j++) {
                if (selected.includes(langData[i].records[j].lang))
                    tempArr.push({ [langData[i].records[j].lang]: langData[i].records[j].occurrence })
            }
            const obj = tempArr.reduce((obj, item) => ({ ...obj, ...item }), {});
            newData.push(obj);
        }
        setSelectedData(newData.reverse());
    }, [langData, selected])

    const findColor = (str) => {
        const lang = str + "randa$a1"
        let hash = 0;
        for (let i = 0; i < lang.length; i++) {
            hash = lang.charCodeAt(i) + ((hash << 5) - hash);
        }
        let colour = '#';
        for (let i = 0; i < 3; i++) {
            let value = (hash >> (i * 8)) & 0xFF;
            colour += ('00' + value.toString(16)).substr(-2);
        }
        return colour;
    }

    const handleChange = (event) => {
        selected.includes(event.target.name) ?
            setSelected(selected.filter(item => item !== event.target.name)) :
            setSelected([...selected, event.target.name])
    }

    const handleChangeSelectAll = (event) => {
        const allSelected = [];
        if (event.target.checked) {
            for (let i = 0; i < totalOccurrence.length; i++) {
                if (totalOccurrence[i][1] >= showLangQty)
                    allSelected.push(totalOccurrence[i][0]);
            }
        }
        setSelected(allSelected)
    }

    const onClickShowAll = () => {
        setshowLangQty(showLangQty === Number.MAX_SAFE_INTEGER ?
            DEFAULT_SHOW_LANG_QTY : Number.MAX_SAFE_INTEGER);

        if (document.getElementById("legend").checked) {
            const allSelected = [];
            for (let i = 0; i < totalOccurrence.length; i++) {
                allSelected.push(totalOccurrence[i][0]);
            }
            setSelected(allSelected)
        }
    }

    return (
        <section className="ChartComponent">
            {totalOccurrence.length > 0 ? (
                <div className="big-wrapper">
                    <div className="legend-wrapper">
                        <div className={showLangQty === DEFAULT_SHOW_LANG_QTY ? "lang-list" : "lang-list max-height"}>
                            <div className="list-row list-header grayed">
                                <input type="checkbox" id="legend" onChange={handleChangeSelectAll} />
                                <label htmlFor="legend" className="label-lang">LANG:</label>
                                <div className="tooltip">
                                    <label htmlFor="legend" className="label-value">OCC:</label>
                                    <span className="tooltip-text">Totla number of occurrences in selected time period. Data gathered from 1% of tweets</span>
                                </div>
                            </div>
                            {totalOccurrence.map((lang, index) => {
                                if (index < showLangQty) {
                                    return (
                                        <div key={lang} className={index % 2 === 0 ? "list-row" : "list-row grayed"} >
                                            <input
                                                type="checkbox"
                                                id={lang[0]}
                                                value={lang[0]}
                                                name={lang[0]}
                                                key={lang + selected}
                                                onChange={handleChange}
                                                defaultChecked={selected.includes(lang[0])} />
                                            <label htmlFor={lang[0]} className="label-lang">{findFullLangName(lang[0])}</label>
                                            <label htmlFor={lang[0]} className="label-value">{lang[1].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")}</label>
                                        </div>
                                    )
                                } else {
                                    return null;
                                }
                            }
                            )}
                        </div>
                        <button className="button-show" onClick={onClickShowAll}>
                            {showLangQty === DEFAULT_SHOW_LANG_QTY ? "SHOW LESS POPULAR" : "HIDE LESS POPULAR"}
                        </button>
                    </div>
                    <div className="chart-wrapper">
                        <div className="chart">
                            <ResponsiveContainer width="99%" height={500} >
                                < LineChart
                                    data={selectedData}
                                >
                                    <defs>
                                        <filter id="shadow" height="200%">
                                            <feDropShadow dx="0" dy="10" stdDeviation="8" />
                                        </filter>
                                        <filter id="glow">
                                            <feGaussianBlur stdDeviation="2.5" result="coloredBlur" />
                                            <feMerge>
                                                <feMergeNode in="coloredBlur" />
                                                <feMergeNode in="SourceGraphic" />
                                            </feMerge>
                                        </filter>
                                    </defs>
                                    <XAxis
                                        stroke='white'
                                        dataKey="timestamp"
                                        type="number"
                                        tickCount={selectedData.length}
                                        domain={['dataMin', 'dataMax']}
                                        tickMargin="5"
                                        tickFormatter={time =>
                                            moment(time).local().format(
                                                ["7D", "30D"].includes(dataSet) ? '"MMM Do YY"' :
                                                    ["1D", "6H"].includes(dataSet) ? 'h:mm a' : 'h:mm:ss')} />
                                    <YAxis
                                        stroke='white'
                                    />
                                    <Tooltip
                                        labelFormatter={time => moment(time).local().format('MMMM Do YYYY, h:mm:ss a')}
                                        formatter={(value) => value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")}
                                    />
                                    {selected.map((lang) =>
                                        <Line
                                            type="monotone"
                                            dataKey={lang} key={lang}
                                            stroke={findColor(lang)}
                                            name={findFullLangName(lang)}
                                            dot={false}
                                            filter="url(#shadow) url(#glow)"
                                        />
                                    )}
                                </LineChart >
                            </ResponsiveContainer>
                        </div>
                        <div className="button-bar">
                            <button className={dataSet === "1H" ? "button-selected" : ""} onClick={() => setDataSet("1H")}>
                                1H
                            </button>
                            <button className={dataSet === "6H" ? "button-selected" : ""} onClick={() => setDataSet("6H")}>
                                6H
                            </button>
                            <button className={dataSet === "1D" ? "button-selected" : ""} onClick={() => setDataSet("1D")}>
                                1D
                            </button>
                            <button className={dataSet === "7D" ? "button-selected" : ""} onClick={() => setDataSet("7D")}>
                                7D
                            </button>
                            <button className={dataSet === "30D" ? "button-selected" : ""} onClick={() => setDataSet("30D")}>
                                30D
                            </button>
                        </div>
                    </div>
                </div>
            ) : (
                <p>
                    loading
                </p>
            )
            }
        </section >
    )
}