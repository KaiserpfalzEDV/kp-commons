/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.google.drive;

/**
 * GoogleWorkspaceMimeTypes -- The defined MimeTypes according to Google Workspace and Drive MIME Types.
 *
 * <p>These are the mime types specified in <a href="https://developers.google.com/drive/api/v3/mime-types">Google
 * Workspace and Drive MIME Types</a>.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-06-03
 */
public interface GoogleWorkspaceMimeTypes {
    String AUDIO = "application/vnd.google-apps.audio";
    String DOCUMENT = "application/vnd.google-apps.document";
    String DRIVE_SDK = "application/vnd.google-apps.drive-sdk";
    String DRAWING = "application/vnd.google-apps.drawing";
    String FILE = "application/vnd.google-apps.file";
    String FOLDER = "application/vnd.google-apps.folder";
    String FORM = "application/vnd.google-apps.form";
    String FUSIONTABLE = "application/vnd.google-apps.fusiontable";
    String MAP = "application/vnd.google-apps.map";
    String PHOTO = "application/vnd.google-apps.photo";
    String PRESENTATION = "application/vnd.google-apps.presentation";
    String SCRIPT = "application/vnd.google-apps.script";
    String SHORTCUT = "application/vnd.google-apps.shortcut";
    String SITE = "application/vnd.google-apps.site";
    String SPREADSHEET = "application/vnd.google-apps.spreadsheet";
    String UNKNOWN = "application/vnd.google-apps.unknown";
    String VIDEO = "application/vnd.google-apps.video";
}
