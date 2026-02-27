{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "ms-search-engine.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "ms-search-engine.labels" -}}
helm.sh/chart: {{ include "ms-search-engine.chart" . }}
{{ include "ms-search-engine.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "ms-search-engine.selectorLabels" -}}
app.kubernetes.io/name: {{ include "ms-search-engine.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}