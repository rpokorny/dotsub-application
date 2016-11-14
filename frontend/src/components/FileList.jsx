import React from 'react';
import moment from 'moment';

import './FileList.css';


const formatDate = date => moment(date).format('MM/DD/YY HH:mm:ss');

const FileRow = ({file}) =>
    <tr>
        <td>
            <a href={file.href} type={file.mediaType}>{file.title}</a>
        </td>
        <td>{file.description}</td>
        <td>{formatDate(file.creationDate)}</td>
    </tr>;

export default function FileList({files}) {
    const fileEls = files.map((f, i) => <FileRow file={f} key={i} />);

    return (
        <table id="file-list">
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Creation Date</th>
                </tr>
            </thead>
            <tbody>
                {fileEls.toArray()}
            </tbody>
        </table>
    );
}
