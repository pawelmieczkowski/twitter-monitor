import "./ChartPage.scss"
import React, { useEffect, useState } from 'react';
import { Chart } from '../components/Chart';
import { Header } from "../components/Header";
import { NetworkError } from "./NetworkError";

export const ChartPage = () => {
    const [langData, setLangData] = useState([]);
    const [totalOccurrence, setTotalOccurrence] = useState([]);
    const [dataSet, setDataSet] = useState("1H")
    const [code, setCode] = useState();

    useEffect(() => {
        const fetchRecords = async () => {
            const response = await fetch(`${process.env.REACT_APP_API_ROOT_URL}/api/v1/records/recent-${dataSet}`);
            const data = await response.json();
            setLangData(data);
        };
        fetchRecords().catch(error => {
            setCode(error.code ? error.code : 'NetworkError');
        });
    }, [dataSet])

    useEffect(() => {
        const map = new Map();
        for (let i = 0; i < langData.length; i++) {
            for (let j = 0; j < langData[i].records.length; j++) {
                let record = langData[i].records[j];
                let currentValue = map.get(record.lang)
                map.set(record.lang, currentValue > 0 ? currentValue + record.occurrence : record.occurrence);
            }
        }
        /* delete undefined tweets from presented data */
        map.delete("und");
        const arr = Array.from(map).sort((a, b) => b[1] - a[1]);
        setTotalOccurrence(arr)
    }, [langData]);


    return (
        <section className="ChartPage">
            {code !== 'NetworkError' ? (
                <div>
                    <Header />
                    {langData.length > 0 && totalOccurrence.length > 0 ? (
                        <Chart langData={langData} totalOccurrence={totalOccurrence} dataSet={dataSet} setDataSet={setDataSet} />
                    ) : (
                        <p>loading</p>
                    )}
                </div>
            ) : (
                <NetworkError />
            )}
        </section>
    )
}