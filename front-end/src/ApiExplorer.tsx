import React, { useState, useEffect } from 'react';
import { 
  Globe, 
  Calendar, 
  MessageSquare, 
  Database, 
  Play, 
  Copy, 
  ExternalLink,
  ChevronDown,
  ChevronRight,
  CheckCircle,
  XCircle,
  AlertCircle,
  Code,
  Settings
} from 'lucide-react';

interface EndpointGroup {
  name: string;
  description: string;
  icon: React.ReactNode;
  endpoints: Endpoint[];
}

interface Endpoint {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE';
  path: string;
  description: string;
  parameters?: Parameter[];
  body?: any;
  example?: string;
  testable: boolean;
}

interface Parameter {
  name: string;
  type: string;
  required: boolean;
  description: string;
  defaultValue?: string;
}

const ApiExplorer: React.FC = () => {
  const [expandedGroups, setExpandedGroups] = useState<Set<string>>(new Set(['holidays']));
  const [testResults, setTestResults] = useState<Map<string, any>>(new Map());
  const [loading, setLoading] = useState<Set<string>>(new Set());
  const [parameterValues, setParameterValues] = useState<Map<string, any>>(new Map());

  const baseUrl = 'http://localhost:8080';

  const endpointGroups: EndpointGroup[] = [
    {
      name: 'holidays',
      description: 'Holiday Management & Query APIs',
      icon: <Calendar className="w-5 h-5" />,
      endpoints: [
        {
          method: 'GET',
          path: '/api/holidays',
          description: 'Get all holidays',
          testable: true,
          example: `${baseUrl}/api/holidays`
        },
        {
          method: 'GET',
          path: '/api/holidays/country/{countryCode}',
          description: 'Get holidays by country',
          parameters: [
            { name: 'countryCode', type: 'string', required: true, description: 'Country code (e.g., TR, US)', defaultValue: 'TR' },
            { name: 'language', type: 'string', required: false, description: 'Language code (en, tr)', defaultValue: 'en' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/country/TR?language=en`
        },
        {
          method: 'GET',
          path: '/api/holidays/country/{countryCode}/year/{year}',
          description: 'Get holidays by country and year',
          parameters: [
            { name: 'countryCode', type: 'string', required: true, description: 'Country code', defaultValue: 'TR' },
            { name: 'year', type: 'number', required: true, description: 'Year', defaultValue: '2025' },
            { name: 'language', type: 'string', required: false, description: 'Language code', defaultValue: 'en' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/country/TR/year/2025?language=en`
        },
        {
          method: 'GET',
          path: '/api/holidays/today',
          description: 'Check if today is a holiday',
          parameters: [
            { name: 'country', type: 'string', required: false, description: 'Country code', defaultValue: 'TR' },
            { name: 'language', type: 'string', required: false, description: 'Language code', defaultValue: 'en' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/today?country=TR&language=en`
        },
        {
          method: 'GET',
          path: '/api/holidays/range',
          description: 'Get holidays in date range',
          parameters: [
            { name: 'start', type: 'date', required: true, description: 'Start date (YYYY-MM-DD)', defaultValue: '2025-01-01' },
            { name: 'end', type: 'date', required: true, description: 'End date (YYYY-MM-DD)', defaultValue: '2025-01-31' },
            { name: 'country', type: 'string', required: false, description: 'Country code', defaultValue: 'TR' },
            { name: 'language', type: 'string', required: false, description: 'Language code', defaultValue: 'en' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/range?start=2025-01-01&end=2025-01-31&country=TR&language=en`
        },
        {
          method: 'GET',
          path: '/api/holidays/working-days',
          description: 'Calculate working days between dates',
          parameters: [
            { name: 'start', type: 'date', required: true, description: 'Start date', defaultValue: '2025-01-01' },
            { name: 'end', type: 'date', required: true, description: 'End date', defaultValue: '2025-01-31' },
            { name: 'country', type: 'string', required: false, description: 'Country code', defaultValue: 'TR' },
            { name: 'includeEndDate', type: 'boolean', required: false, description: 'Include end date', defaultValue: 'true' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/working-days?start=2025-01-01&end=2025-01-31&country=TR&includeEndDate=true`
        },
        {
          method: 'GET',
          path: '/api/holidays/types',
          description: 'Get holiday types',
          testable: true,
          example: `${baseUrl}/api/holidays/types`
        },
        {
          method: 'GET',
          path: '/api/holidays/types/translated',
          description: 'Get translated holiday types',
          parameters: [
            { name: 'language', type: 'string', required: false, description: 'Language code', defaultValue: 'en' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/types/translated?language=en`
        },
        {
          method: 'GET',
          path: '/api/holidays/{id}',
          description: 'Get holiday by ID',
          parameters: [
            { name: 'id', type: 'number', required: true, description: 'Holiday ID', defaultValue: '1' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/1`
        },
        {
          method: 'POST',
          path: '/api/holidays',
          description: 'Create new holiday',
          body: {
            name: 'New Holiday',
            date: '2025-12-25',
            countryCode: 'TR',
            type: 'PUBLIC'
          },
          testable: false,
          example: 'POST with JSON body'
        },
        {
          method: 'PUT',
          path: '/api/holidays/{id}',
          description: 'Update holiday',
          parameters: [
            { name: 'id', type: 'number', required: true, description: 'Holiday ID' }
          ],
          testable: false,
          example: 'PUT with JSON body'
        },
        {
          method: 'DELETE',
          path: '/api/holidays/{id}',
          description: 'Delete holiday',
          parameters: [
            { name: 'id', type: 'number', required: true, description: 'Holiday ID' }
          ],
          testable: false,
          example: 'DELETE request'
        }
      ]
    },
    {
      name: 'audiences',
      description: 'Audience Management APIs',
      icon: <Globe className="w-5 h-5" />,
      endpoints: [
        {
          method: 'GET',
          path: '/api/holidays/audiences',
          description: 'Get all audiences',
          testable: true,
          example: `${baseUrl}/api/holidays/audiences`
        },
        {
          method: 'GET',
          path: '/api/holidays/audiences/translated',
          description: 'Get translated audiences',
          parameters: [
            { name: 'language', type: 'string', required: false, description: 'Language code', defaultValue: 'en' }
          ],
          testable: true,
          example: `${baseUrl}/api/holidays/audiences/translated?language=en`
        },
        {
          method: 'POST',
          path: '/api/holidays/audiences',
          description: 'Create new audience',
          body: {
            code: 'NEW_AUD',
            audienceName: 'New Audience'
          },
          testable: false,
          example: 'POST with JSON body'
        },
        {
          method: 'PUT',
          path: '/api/holidays/audiences/{audienceId}',
          description: 'Update audience',
          parameters: [
            { name: 'audienceId', type: 'number', required: true, description: 'Audience ID' }
          ],
          testable: false,
          example: 'PUT with JSON body'
        },
        {
          method: 'DELETE',
          path: '/api/holidays/audiences/{audienceId}',
          description: 'Delete audience',
          parameters: [
            { name: 'audienceId', type: 'number', required: true, description: 'Audience ID' }
          ],
          testable: false,
          example: 'DELETE request'
        }
      ]
    },
    {
      name: 'countries',
      description: 'Country Information APIs',
      icon: <Globe className="w-5 h-5" />,
      endpoints: [
        {
          method: 'GET',
          path: '/api/countries',
          description: 'Get all countries',
          testable: true,
          example: `${baseUrl}/api/countries`
        }
      ]
    },
    {
      name: 'chat',
      description: 'AI Chat & Query APIs',
      icon: <MessageSquare className="w-5 h-5" />,
      endpoints: [
        {
          method: 'POST',
          path: '/api/chat',
          description: 'Chat with AI about holidays',
          body: {
            message: 'What holidays are there in January 2025?',
            country: 'TR',
            language: 'en'
          },
          testable: true,
          example: 'POST with JSON body containing message, country, and language'
        }
      ]
    },
    {
      name: 'templates',
      description: 'Holiday Template APIs',
      icon: <Database className="w-5 h-5" />,
      endpoints: [
        {
          method: 'GET',
          path: '/api/holiday-templates',
          description: 'Get all holiday templates',
          testable: true,
          example: `${baseUrl}/api/holiday-templates`
        }
      ]
    }
  ];

  const toggleGroup = (groupName: string) => {
    const newExpanded = new Set(expandedGroups);
    if (newExpanded.has(groupName)) {
      newExpanded.delete(groupName);
    } else {
      newExpanded.add(groupName);
    }
    setExpandedGroups(newExpanded);
  };

  const getMethodColor = (method: string) => {
    switch (method) {
      case 'GET': return 'bg-green-100 text-green-800 border-green-200';
      case 'POST': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'PUT': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'DELETE': return 'bg-red-100 text-red-800 border-red-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const buildUrl = (endpoint: Endpoint) => {
    let url = baseUrl + endpoint.path;
    const params = new URLSearchParams();
    
    if (endpoint.parameters) {
      endpoint.parameters.forEach(param => {
        const key = `${endpoint.path}-${param.name}`;
        const value = parameterValues.get(key) || param.defaultValue || '';
        
        if (param.name.includes('{') || param.name.includes('}')) return; // Skip path parameters
        
        if (value) {
          params.append(param.name, value);
        }
      });
    }
    
    // Handle path parameters
    endpoint.parameters?.forEach(param => {
      if (endpoint.path.includes(`{${param.name}}`)) {
        const key = `${endpoint.path}-${param.name}`;
        const value = parameterValues.get(key) || param.defaultValue || '';
        url = url.replace(`{${param.name}}`, value);
      }
    });
    
    const queryString = params.toString();
    return queryString ? `${url}?${queryString}` : url;
  };

  const testEndpoint = async (endpoint: Endpoint) => {
    const endpointKey = `${endpoint.method}-${endpoint.path}`;
    setLoading(new Set([...loading, endpointKey]));
    
    try {
      const url = buildUrl(endpoint);
      console.log(`Testing endpoint: ${url}`);
      let response;
      
      const startTime = Date.now();
      
      if (endpoint.method === 'POST' && endpoint.body) {
        console.log(`Sending POST request with body:`, endpoint.body);
        response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(endpoint.body)
        });
      } else {
        response = await fetch(url, {
          method: endpoint.method
        });
      }
      
      const endTime = Date.now();
      const responseTime = endTime - startTime;
      console.log(`Response received in ${responseTime}ms, status: ${response.status}`);
      
      const data = await response.json();
      
      setTestResults(new Map([...testResults, [endpointKey, {
        success: response.ok,
        status: response.status,
        data: data,
        url: url,
        responseTime: responseTime
      }]]));
    } catch (error) {
      console.error(`Error testing endpoint:`, error);
      setTestResults(new Map([...testResults, [endpointKey, {
        success: false,
        error: (error instanceof Error ? error.message : String(error)),
        url: buildUrl(endpoint)
      }]]));
    } finally {
      const newLoading = new Set(loading);
      newLoading.delete(endpointKey);
      setLoading(newLoading);
    }
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
  };

  const updateParameter = (endpointPath: string, paramName: string, value: string) => {
    const key = `${endpointPath}-${paramName}`;
    const newValues = new Map(parameterValues);
    newValues.set(key, value);
    setParameterValues(newValues);
  };

  return (
    <div className="max-w-7xl mx-auto p-6 bg-white dark:bg-gray-900">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          Holiday API Explorer
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          Interactive documentation and testing interface for your Holiday API endpoints
        </p>
        <div className="mt-4 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
          <div className="flex items-center gap-2 mb-2">
            <Settings className="w-4 h-4 text-blue-600" />
            <span className="font-medium text-blue-900 dark:text-blue-100">Base URL:</span>
          </div>
          <code className="text-blue-800 dark:text-blue-200 font-mono">{baseUrl}</code>
        </div>
      </div>

      <div className="space-y-6">
        {endpointGroups.map((group) => (
          <div key={group.name} className="border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden">
            <div 
              className="bg-gray-50 dark:bg-gray-800 p-4 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              onClick={() => toggleGroup(group.name)}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  {group.icon}
                  <div>
                    <h2 className="text-lg font-semibold text-gray-900 dark:text-white">
                      {group.name.charAt(0).toUpperCase() + group.name.slice(1)}
                    </h2>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {group.description}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <span className="bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200 px-2 py-1 rounded-full text-sm font-medium">
                    {group.endpoints.length} endpoints
                  </span>
                  {expandedGroups.has(group.name) ? 
                    <ChevronDown className="w-5 h-5 text-gray-500" /> : 
                    <ChevronRight className="w-5 h-5 text-gray-500" />
                  }
                </div>
              </div>
            </div>

            {expandedGroups.has(group.name) && (
              <div className="divide-y divide-gray-200 dark:divide-gray-700">
                {group.endpoints.map((endpoint, index) => {
                  const endpointKey = `${endpoint.method}-${endpoint.path}`;
                  const result = testResults.get(endpointKey);
                  const isLoading = loading.has(endpointKey);

                  return (
                    <div key={index} className="p-6 bg-white dark:bg-gray-900">
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex items-center gap-3">
                          <span className={`px-3 py-1 rounded-md text-sm font-medium border ${getMethodColor(endpoint.method)}`}>
                            {endpoint.method}
                          </span>
                          <code className="text-gray-900 dark:text-gray-100 font-mono text-sm bg-gray-100 dark:bg-gray-800 px-2 py-1 rounded">
                            {endpoint.path}
                          </code>
                        </div>
                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => copyToClipboard(buildUrl(endpoint))}
                            className="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 transition-colors"
                            title="Copy URL"
                          >
                            <Copy className="w-4 h-4" />
                          </button>
                          {endpoint.testable && (
                            <button
                              onClick={() => testEndpoint(endpoint)}
                              disabled={isLoading}
                              className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                            >
                              {isLoading ? (
                                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                              ) : (
                                <Play className="w-4 h-4" />
                              )}
                              Test
                            </button>
                          )}
                        </div>
                      </div>

                      <p className="text-gray-700 dark:text-gray-300 mb-4">{endpoint.description}</p>

                      {endpoint.parameters && endpoint.parameters.length > 0 && (
                        <div className="mb-4">
                          <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-2">Parameters:</h4>
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                            {endpoint.parameters.map((param) => (
                              <div key={param.name} className="flex flex-col">
                                <label className="text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">
                                  {param.name}
                                  {param.required && <span className="text-red-500 ml-1">*</span>}
                                  <span className="text-gray-500 ml-1">({param.type})</span>
                                </label>
                                <input
                                  type={param.type === 'date' ? 'date' : 'text'}
                                  value={parameterValues.get(`${endpoint.path}-${param.name}`) || param.defaultValue || ''}
                                  onChange={(e) => updateParameter(endpoint.path, param.name, e.target.value)}
                                  placeholder={param.description}
                                  className="px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md text-sm bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {endpoint.body && (
                        <div className="mb-4">
                          <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-2">Request Body:</h4>
                          <pre className="bg-gray-100 dark:bg-gray-800 p-3 rounded-md text-sm overflow-x-auto">
                            <code className="text-gray-900 dark:text-gray-100">
                              {JSON.stringify(endpoint.body, null, 2)}
                            </code>
                          </pre>
                        </div>
                      )}

                      <div className="mb-4">
                        <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-2">Example URL:</h4>
                        <div className="flex items-center gap-2 bg-gray-100 dark:bg-gray-800 p-3 rounded-md">
                          <code className="text-gray-900 dark:text-gray-100 text-sm flex-1 overflow-x-auto">
                            {buildUrl(endpoint)}
                          </code>
                          <button
                            onClick={() => window.open(buildUrl(endpoint), '_blank')}
                            className="p-1 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                            title="Open in new tab"
                          >
                            <ExternalLink className="w-4 h-4" />
                          </button>
                        </div>
                      </div>

                      {result && (
                        <div className="mt-4">
                          <div className="flex items-center gap-2 mb-2">
                            {result.success ? (
                              <CheckCircle className="w-4 h-4 text-green-600" />
                            ) : (
                              <XCircle className="w-4 h-4 text-red-600" />
                            )}
                            <span className="text-sm font-medium text-gray-900 dark:text-white">
                              Response {result.status && `(${result.status})`}
                              {result.responseTime && ` - ${result.responseTime}ms`}:
                            </span>
                          </div>
                          <pre className={`p-3 rounded-md text-sm overflow-x-auto ${
                            result.success 
                              ? 'bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800' 
                              : 'bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800'
                          }`}>
                            <code className={result.success ? 'text-green-900 dark:text-green-100' : 'text-red-900 dark:text-red-100'}>
                              {result.data ? JSON.stringify(result.data, null, 2) : result.error}
                            </code>
                          </pre>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ApiExplorer;
